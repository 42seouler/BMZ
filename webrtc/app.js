const express = require("express");
const http = require("http");

const PORT = process.env.PORT || 80;

const app = express();
const server = http.createServer(app);
const io = require("socket.io")(server)

app.use(express.static("public"));

app.get("/", (req, res) => {
  res.sendFile(__dirname + "/public/index.html");
});

let connectedPeers = [];
let connectedPeersClusterB = new Map();
let connectedPeersClusterA = new Map();

io.on("connection", (socket) => {
  connectedPeers.push(socket.id);
  let data = {
    totalPeers: connectedPeers.length,
    clusterA: connectedPeersClusterA.size,
    clusterB: connectedPeersClusterB.size
  };
  io.emit('connected-peers-status', data);

  socket.on("pre-offer", (data) => {
    const { calleePersonalCode, callType } = data;
    const connectedPeer = connectedPeers.find(
      (peerSocketId) => peerSocketId === calleePersonalCode
    );

    if (connectedPeer) {
      const data = {
        callerSocketId: socket.id,
        callType,
      };
      io.to(calleePersonalCode).emit("pre-offer", data);
    } else {
      const data = {
        preOfferAnswer: "CALLEE_NOT_FOUND",
      };
      io.to(socket.id).emit("pre-offer-answer", data);
    }
  });

  socket.on("pre-offer-answer", (data) => {
    const { callerSocketId } = data;

    const connectedPeer = connectedPeers.find(
      (peerSocketId) => peerSocketId === callerSocketId
    );
    // 전화 연결이 되면 false로 상태 변경
    if (connectedPeersClusterA.has(callerSocketId)) {
      connectedPeersClusterA.set(callerSocketId, false);
    }
    if (connectedPeersClusterB.has(callerSocketId)) {
      connectedPeersClusterB.set(callerSocketId, false);
    }
    if (connectedPeer) {
      io.to(data.callerSocketId).emit("pre-offer-answer", data);
    }
  });

  socket.on("webRTC-signaling", (data) => {
    const { connectedUserSocketId } = data;

    const connectedPeer = connectedPeers.find(
      (peerSocketId) => peerSocketId === connectedUserSocketId
    );

    if (connectedPeer) {
      io.to(connectedUserSocketId).emit("webRTC-signaling", data);
    }
  });

  socket.on("user-hanged-up", (data) => {
    const { connectedUserSocketId } = data;

    const connectedPeer = connectedPeers.find(
      (peerSocketId) => peerSocketId === connectedUserSocketId
    );

    // 통화 가능 상태로 변경하기
    // 본인
    if (connectedPeersClusterA.has(socket.id)) {
      connectedPeersClusterA.set(socket.id, true);
    }
    if (connectedPeersClusterB.has(socket.id)) {
      connectedPeersClusterB.set(socket.id, true);
    }
    // 상대방
    if (connectedPeersClusterA.has(connectedPeer)) {
      connectedPeersClusterA.set(connectedPeer, true);
    }
    if (connectedPeersClusterB.has(connectedPeer)) {
      connectedPeersClusterB.set(connectedPeer, true);
    }

    if (connectedPeer) {
      io.to(connectedUserSocketId).emit("user-hanged-up");
    }
  });

  socket.on("cluster-a-connection-status", (data) => {
    const { status } = data;
    if (status) {
      connectedPeersClusterA.set(socket.id, true);
    } else {
      connectedPeersClusterA.delete(socket.id);
    }
    io.emit('cluster-a-status', connectedPeersClusterA.size);
  });

  // ClusterB 대기열에 추가 또는 제거
  socket.on("cluster-b-connection-status", (data) => {
    const { status } = data;
    if (status) {
      connectedPeersClusterB.set(socket.id, true);
    } else {
      connectedPeersClusterB.delete(socket.id);
    }
    io.emit('cluster-b-status', connectedPeersClusterB.size);
  });

  socket.on("get-cluster-a-socket-id", () => {

    let randomStrangerSocketId;

    // 맵에서 통과 가능한 인원만 추출
    let filteredConnectedPeersClusterA = [];
    for (let [key, value] of connectedPeersClusterA) {
      if (key !== socket.id && value === true) {
        filteredConnectedPeersClusterA.push(key);
      }
    }

    if (filteredConnectedPeersClusterA.length > 0) {
      randomStrangerSocketId =
          filteredConnectedPeersClusterA[
              Math.floor(Math.random() * filteredConnectedPeersClusterA.length)
              ];
    } else {
      randomStrangerSocketId = null;
    }

    const data = {
      randomStrangerSocketId,
    };
    io.to(socket.id).emit("stranger-socket-id", data);
  });

  // ClusterB에서 랜덤으로 카뎃 소켓 얻기
  socket.on("get-cluster-b-socket-id", () => {
    let randomStrangerSocketId;

    // 맵에서 통과 가능한 인원만 추출
    let filteredConnectedPeersClusterB = [];
    for (let [key, value] of connectedPeersClusterB) {
      if (key !== socket.id && value === true) {
        filteredConnectedPeersClusterB.push(key);
      }
    }

    if (filteredConnectedPeersClusterB.length > 0) {
      randomStrangerSocketId =
          filteredConnectedPeersClusterB[
              Math.floor(Math.random() * filteredConnectedPeersClusterB.length)
              ];
    } else {
      randomStrangerSocketId = null;
    }

    const data = {
      randomStrangerSocketId,
    };

    io.to(socket.id).emit("stranger-socket-id", data);
  });

  // 소켓 연결이 끊어지면 대기열에서 제거 및 소켓에게 전파
  socket.on("disconnect", () => {
    const newConnectedPeers = connectedPeers.filter(
      (peerSocketId) => peerSocketId !== socket.id
    );
    connectedPeers = newConnectedPeers;

    if (connectedPeersClusterA.has(socket.id)) {
      connectedPeersClusterA.delete(socket.id);
    }
    if (connectedPeersClusterB.has(socket.id)) {
      connectedPeersClusterB.delete(socket.id);
    }
    data = {
      totalPeers: connectedPeers.length,
      clusterA: connectedPeersClusterA.size,
      clusterB: connectedPeersClusterB.size
    }
    io.emit('disconnect-status', data);
  });

  // 6.15 일 추가 부분
  socket.on("change-call-status-false", () => {
    if (connectedPeersClusterA.has(socket.id)) {
      connectedPeersClusterA.set(socket.id, false);
    }
    if (connectedPeersClusterB.has(socket.id)) {
      connectedPeersClusterB.set(socket.id, false);
    }
  });
});

server.listen(PORT, () => {
  console.log(`listening on ${PORT}`);
});
