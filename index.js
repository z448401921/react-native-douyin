import {NativeModules} from 'react-native';

const {DouYinModule} = NativeModules;

export default Douyin={
  init(appKey){
    DouYinModule.init(appKey)
  },
  auth(scope,state){
    return DouYinModule.auth(scope,state)
  },
  shareVideo(path,publish){
    return DouYinModule.shareVideo(path,publish)
  }
};
