import * as store from "./store.js";
import * as ui from "./ui.js";
import * as webRTCHandler from "./webRTCHandler.js";
import * as constants from "./constants.js";
import * as strangerUtils from "./strangerUtils.js";

let socketIO = null;

export const registerSocketEvents = (socket) => {
  socketIO = socket;

  socket.on("connect", () => {
    store.setSocketId(socket.id);
    ui.updatePersonalCode(socket.id);
  });

  socket.on("connected-peers-status", (data) => {
    ui.updateStatus(data);
  });

  socket.on("disconnect-status", (data) => {
    ui.updateStatus(data);
  });

  socket.on("cluster-a-status", (data) => {
    ui.clusterAStatus(data);
  });

  socket.on("cluster-b-status", (data) => {
    ui.clusterBStatus(data);
  });

  socket.on("pre-offer", (data) => {
    webRTCHandler.handlePreOffer(data);
  });

  socket.on("pre-offer-answer", (data) => {
    webRTCHandler.handlePreOfferAnswer(data);
  });

  socket.on("user-hanged-up", () => {
    webRTCHandler.handleConnectedUserHangedUp();
  });

  socket.on("webRTC-signaling", (data) => {
    switch (data.type) {
      case constants.webRTCSignaling.OFFER:
        webRTCHandler.handleWebRTCOffer(data);
        break;
      case constants.webRTCSignaling.ANSWER:
        webRTCHandler.handleWebRTCAnswer(data);
        break;
      case constants.webRTCSignaling.ICE_CANDIDATE:
        webRTCHandler.handleWebRTCCandidate(data);
        break;
      default:
        return;
    }
  });

  socket.on("stranger-socket-id", (data) => {
    strangerUtils.connectWithStranger(data);
  });

};

export const sendPreOffer = (data) => {
  socketIO.emit("pre-offer", data);
};

export const sendPreOfferAnswer = (data) => {
  socketIO.emit("pre-offer-answer", data);
};

export const sendDataUsingWebRTCSignaling = (data) => {
  socketIO.emit("webRTC-signaling", data);
};

export const sendUserHangedUp = (data) => {
  socketIO.emit("user-hanged-up", data);
};

// ClusterA 소켓 대기열에 추가 및 제거
export const changeClusterAConnectionStatus = (data) => {
  socketIO.emit("cluster-a-connection-status", data);
};

// ClusterB 소켓 대기열 추가 및 제거
export const changeClusterBConnectionStatus = (data) => {
  socketIO.emit("cluster-b-connection-status", data);
};

// ClusterA 소켓 대기열에서 소켓 얻기
export const getClusterASocketId = () => {
  socketIO.emit("get-cluster-a-socket-id");
};

// ClusterB 소켓 대기열에서 소켓 얻기
export const getClusterBSocketId = () => {
  socketIO.emit("get-cluster-b-socket-id");
};