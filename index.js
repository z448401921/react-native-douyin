import { NativeModules } from 'react-native';

const { DouYinModule } = NativeModules;

const Douyin = {
  /**
   * 初始化抖音 SDK
   * @param {string} appKey - 应用的 AppKey
   */
  init(appKey) {
    if (!appKey) {
      console.warn('Douyin.init: appKey is required');
      return;
    }
    DouYinModule.init(appKey);
  },

  /**
   * 授权登录
   * @param {string} scope - 权限范围
   * @param {string} state - 客户端状态码
   * @returns {Promise<any>}
   */
  auth(scope, state) {
    return DouYinModule.auth(scope, state);
  },

  /**
   * 分享视频
   * @param {string} path - 本地视频路径
   * @param {boolean} publish - 是否发布（true 为自动发布）
   * @returns {Promise<any>}
   */
  shareVideo(path, publish) {
    return DouYinModule.shareVideo(path, publish);
  }
};

export default Douyin;
