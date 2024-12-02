import torch
import cv2
from pyzbar import pyzbar
from flask import request, jsonify
import pandas as pd
# import mysql.connector
import winsound
import pyttsx3

items = pd.read_csv(r"C:\Users\harsh\OneDrive\Desktop\Final Year Project\webcam (2)\webcam\Items Database - Sheet1.csv")

device = torch.device('cuda:0' if torch.cuda.is_available() else 'cpu')
model = torch.hub.load('ultralytics/yolov5', 'custom', path='model.pt').to(device)

import firebase_admin
from firebase_admin import credentials, firestore
from datetime import datetime

# Initialize Firebase Admin SDK
cred = credentials.Certificate(r"C:\Users\harsh\Downloads\swiftcart-c6125-firebase-adminsdk-teyoz-4799521236.json")
firebase_admin.initialize_app(cred)
cap = cv2.VideoCapture(0)

first_y_coordinate = None
hand_event = ""
y1 = 0
fl = 0

while cap.isOpened():
    _, frame = cap.read()
    results = model(frame)
    detections = results.pandas().xyxy[0]
    
    # Extract the coordinates, class id, and confidence for each detection
    if detections.empty:
      first_y_coordinate = None
    
    for i, detection in detections.iterrows():
        x1, y1, x2, y2 = detection[['xmin', 'ymin', 'xmax', 'ymax']]
        x1, y1, x2, y2 = [round(num) for num in [x1, y1, x2, y2]]

        class_id = detection['class']
        confidence = detection['confidence']
        #print(f'Detection {i}: class {class_id}, confidence {confidence}, bbox [{x1}, {y1}, {x2}, {y2}]')

        # Draw bounding box on input image  
        cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)

        # Put label text on bounding box
        label = f'{"Barcode"} {confidence:.2f}'
        label_size, baseline = cv2.getTextSize(label, cv2.FONT_HERSHEY_SIMPLEX, 0.5, 1)
        cv2.rectangle(frame, (x1, y1), (x1 + label_size[0], y1 - label_size[1] - baseline), (0, 255, 0), cv2.FILLED)
        cv2.putText(frame, label, (x1, y1 - baseline), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 0), 1)

        # Crop image to bounding box of first detected object
        cropped_img = frame[y1:y2, x1:x2]
        gray = cv2.cvtColor(cropped_img, cv2.COLOR_BGR2GRAY)
        barcodes = pyzbar.decode(gray)
        
        # first_y_coordinate
        if first_y_coordinate is None: 
          first_y_coordinate = y1
                  
        for barcode in barcodes:
            data = barcode.data.decode("utf-8")

            # Put barcode data on bounding box
            data_size, baseline = cv2.getTextSize(data, cv2.FONT_HERSHEY_SIMPLEX, 0.5, 1)
            cv2.rectangle(frame, (x1, y2), (x1 + data_size[0], y2 + data_size[1]), (0, 255, 0), cv2.FILLED)
            cv2.putText(frame, data, (x1, y2 + data_size[1]), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 0), 1)

            if data == '':
                continue 

            try:
                # Initialize Firestore
                db = firestore.client()
                
                result = items[items['Barcode'] == data]
                
                if abs(first_y_coordinate - 0) < abs(first_y_coordinate - frame.shape[0]):
                    hand_event = "adding"
                    
                    # For beep sound
                    frequency = 2500  # Set frequency to 2500 Hertz
                    duration = 500  # Set duration to 1000 milliseconds = 1 second
                    winsound.Beep(frequency, duration)
                    
                    # Add data to a collection
                    
                    if not result.empty:
                      data_to_add = {
                        "name": result.iloc[0]['product_Name'],
                        "imgUrl": result.iloc[0]['img_Url'],
                        "event": hand_event
                        }
                    else:
                      print('Error')
                      
                    
                    engine = pyttsx3.init()
                    engine.say(result.iloc[0]['product_Name'] + " " + "adding")
                    engine.runAndWait()
                    
                    #quantity change in items_stock database
                    doc_ref = db.collection("Items_Stock").document(data)

                    # Get the document snapshot
                    doc = doc_ref.get()

                    # Check if the document exists
                    if doc.exists:
                        # Get the current quantity value
                        current_quantity = doc.to_dict().get("Quantity", 0)

                        # Decrement the quantity by 1
                        updated_quantity = current_quantity - 1
                        if updated_quantity<=0:
                            updated_quantity = 0

                        # Update the document with the new quantity value
                        doc_ref.update({"Quantity": updated_quantity})
                    
                    
                    print(data_to_add)
                    # Add a new document with a generated ID
                    def generate_custom_document_id():
                        # Generate a custom document ID based on the current timestamp
                        timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
                        return f"{timestamp}"

                    def store_data_in_firestore(collection_name, document_data):
                        try:
                            # Generate a new custom document ID
                            custom_document_id = generate_custom_document_id()
                            print(custom_document_id)

                            # Reference to the Firestore collection and document with custom ID
                            doc_ref = db.collection(collection_name).document(custom_document_id)

                            # Set the document data
                            doc_ref.set(document_data)

                            print(f"Document added with ID: {custom_document_id}")

                            return True
                        except Exception as e:
                            print(f"Error storing data in Firestore: {e}")
                            return False

                    store_data_in_firestore("705632441947", data_to_add)
                    first_y_coordinate = None
                    import time
                    time.sleep(1)
                else:
                    hand_event = "deleting"
                    
                    frequency = 3500  # Set frequency to 2500 Hertz
                    duration = 1000  # Set duration to 1000 milliseconds = 1 second
                    winsound.Beep(frequency, duration)
                    
                    a_name = "name"
                    a_value = result.iloc[0]['product_Name']
                    query = db.collection("705632441947").where(a_name, "==", a_value).stream()
                    
                    engine = pyttsx3.init()
                    engine.say(a_value+"deleting")
                    engine.runAndWait()
                    
                    doc_ref = db.collection("Items_Stock").document(data)

                    # Get the document snapshot
                    doc = doc_ref.get()

                    # Check if the document exists
                    if doc.exists:
                        # Get the current quantity value
                        current_quantity = doc.to_dict().get("Quantity", 0)

                        # Increment the quantity by 1
                        updated_quantity = current_quantity + 1

                        # Update the document with the new quantity value
                        doc_ref.update({"Quantity": updated_quantity})
                    
                    if not result.empty:
                      data_to_add = {
                        "name": result.iloc[0]['product_Name'],
                        "imgUrl": result.iloc[0]['img_Url'],
                        "event": hand_event
                        }
                    else:
                      print('Error')
                      
                    print(data_to_add)
                    for doc in query:
                        doc.reference.delete()
                        break
                    first_y_coordinate = None
            except Exception as e:
                print('error ', e)

            finally:
                pass
                #conn.close()
                
    # Display annotated image
    cv2.putText(frame, f'First Y Coordinate: {first_y_coordinate}', (10, 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1)
    cv2.putText(frame, f'Current Y Coordinate: {y1}', (10, 40), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1)
    cv2.imshow('Result', frame)
    if cv2.waitKey(1) & ord('q') == 27:
        cv2.destroyAllWindows()
        break