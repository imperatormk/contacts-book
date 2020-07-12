const functions = require('firebase-functions')
const admin = require('firebase-admin')
admin.initializeApp(functions.config().firebase)

const db = admin.firestore()

exports.initializeUserData = functions.auth.user().onCreate(async (user) => {
  const res = await db.collection('users').doc(user.id).set({
    contacts: []
  })
  functions.logger.log('inited user', user.id, res)
})