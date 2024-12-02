import firebase_admin
from firebase_admin import credentials, firestore

# Initialize Firebase Admin SDK
cred = credentials.Certificate(r"C:\Users\bitra\Downloads\swiftcart-c6125-firebase-adminsdk-teyoz-0ddc00fe54.json")
firebase_admin.initialize_app(cred)

# Initialize Firestore
db = firestore.client()

# Add data to a collection
data = {
    "name": "John Doe",
    "age": 30,
    "email": "johndoe@example.com"
}

# Add a new document with a generated ID
db.collection("705632085943").add(data)