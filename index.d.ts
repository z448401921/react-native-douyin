/*
 * @Author: 唉诺
 * @Date: 2021-04-30 23:31:46
 */
declare module "react-native-douyin" {
  export function init(appKey: string):void;
  export function auth(scope: string,state:string): Promise<any>;
  export function share(path: string,isPublish:string): Promise<any>;
}
