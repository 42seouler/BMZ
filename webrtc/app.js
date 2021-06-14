const express = require("express");
const http = require("http");

const PORT = process.env.PORT || 80;

const app = express();
const server = http.createServer(app);
const io = require("socket.io")(server);

app.use(express.static("public"));

app.get("/", (req, res) => {
  res.sendFile(__dirname + "/public/index.html");
});

let connectedPeers = [];
let connectedPeersClusterA = [];
let connectedPeersClusterB = [];

io.on("connection", (socket) => {
  connectedPeers.push(socket.id);
  let data = {
    totalPeers: connectedPeers.length,
    clusterA: connectedPeersClusterA.length,
    clusterB: connectedPeersClusterB.length
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

    if (connectedPeer) {
      io.to(connectedUserSocketId).emit("user-hanged-up");
    }
  });

  // ClusterA 대기열에 추가 또는 제거
  socket.on("cluster-a-connection-status", (data) => {
    const { status } = data;
    if (status) {
      connectedPeersClusterA.push(socket.id);
    } else {
      connectedPeersClusterA = connectedPeersClusterA.filter(
          (peerSocketId) => peerSocketId !== socket.id
      );
    }
    io.emit('cluster-a-status', connectedPeersClusterA.length);
  });

  // ClusterB 대기열에 추가 또는 제거
  socket.on("cluster-b-connection-status", (data) => {
    const { status } = data;
    if (status) {
      connectedPeersClusterB.push(socket.id);
    } else {
      connectedPeersClusterB = connectedPeersClusterB.filter(
          (peerSocketId) => peerSocketId !== socket.id
      );
    }
    io.emit('cluster-b-status', connectedPeersClusterB.length);
  });

  // ClusterA에서 랜덤으로 카뎃 소켓 얻기
  socket.on("get-cluster-a-socket-id", () => {
    let randomStrangerSocketId;
    const filteredConnectedPeersClusterA = connectedPeersClusterA.filter(
      (peerSocketId) => peerSocketId !== socket.id
    );

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
    const filteredConnectedPeersClusterB = connectedPeersClusterB.filter(
        (peerSocketId) => peerSocketId !== socket.id
    );

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

    const newConnectedPeersStrangers = connectedPeersClusterA.filter(
      (peerSocketId) => peerSocketId !== socket.id
    );
    connectedPeersClusterA = newConnectedPeersStrangers;

    const newConnectedPeersClusterB = connectedPeersClusterB.filter(
        (peerSocketId) => peerSocketId !== socket.id
    );
    connectedPeersClusterB = newConnectedPeersClusterB;
    data = {
      totalPeers: connectedPeers.length,
      clusterA: connectedPeersClusterA.length,
      clusterB: connectedPeersClusterB.length
    }
    io.emit('disconnect-status', data);
  });
});

server.listen(PORT, () => {
  console.log(`listening on ${PORT}`);
});
