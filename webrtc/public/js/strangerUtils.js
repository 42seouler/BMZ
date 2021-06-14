import * as wss from "./wss.js";
import * as webRTCHandler from "./webRTCHandler.js";
import * as ui from "./ui.js";

let strangerCallType;

export const changeStrangerConnectionStatus = (status) => {
  const data = { status };
  wss.changeClusterAConnectionStatus(data);
};

// ClusterB Connection status
export const changeClusterBConnectionStatus = (status) => {
  const data = { status };
  wss.changeClusterBConnectionStatus(data);
};

export const getStrangerSocketIdAndConnect = (data) => {
  const { callType, clusterB } = data;
  strangerCallType = callType;
  if (clusterB === true) {
    wss.getClusterBSocketId();
  } else {
    wss.getClusterASocketId();
  }
};

export const connectWithStranger = (data) => {
  if (data.randomStrangerSocketId) {
    webRTCHandler.sendPreOffer(strangerCallType, data.randomStrangerSocketId);
  } else {
    // no user is available for connection
    ui.showNoStrangerAvailableDialog();
  }
};
