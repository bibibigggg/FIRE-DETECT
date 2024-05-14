const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendFCMOnDatabaseWrite = functions.database.ref('/Detected/{DetectedId}')
    .onWrite((change, context) => {
        const newValue = change.after.val();

        // 데이터베이스 업데이트를 감지하고 FCM 메시지 작성
        if (newValue) {
            // 새 데이터가 감지되면 FCM 메시지 보내기
            const message = {
                data: {
                    title: '화재발생!',
                    body: '화재가 발생했습니다.'
                },
                token: 'd7J3whkWTz2XtXzm-Ls_Bu:APA91bHkWSMyU6sQJ90RBnbPmxIOIoZAxHqQi7b9ytE1NWx50QQr1UB0v-EZ-jVrzgKVfHwoXoj65cs4mBaGv-8kbPgAedP1x5HbAAhd8WdzDbuq5yduoBuaxqSLixoDpzrAD6acq277' // 대상 디바이스의 FCM 토큰을 여기에 추가
            };

            return admin.messaging().send(message)
                .then((response) => {
                    console.log('FCM 메시지가 성공적으로 전송되었습니다:', response);
                    return null;
                })
                .catch((error) => {
                    console.error('FCM 메시지 전송 중 오류 발생:', error);
                });
        }
    });

