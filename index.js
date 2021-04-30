import {NativeModules} from 'react-native';

const {DouYinModule} = NativeModules;

export default Douyin={
  init(appKey){
    NativeModules.init(appKey)
  },
  auth(scope,state){
    return NativeModules.auth(scope,state)
  },
  shareVideo(path,publish){
    return NativeModules.shareVideo(path,publish)
  }
};
