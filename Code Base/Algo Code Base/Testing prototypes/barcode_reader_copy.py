import torch
import cv2
from pyzbar import pyzbar 
from flask import request, jsonify
import mysql.connector

device = torch.device('cuda:0' if torch.cuda.is_available() else 'cpu')
model = torch.hub.load('ultralytics/yolov5', 'custom',path='model.pt').to(device)

cap = cv2.VideoCapture(0)
while cap.isOpened():
	_, frame = cap.read()
	results = model(frame)
	detections = results.pandas().xyxy[0]

	# Extract the coordinates, class id, and confidence for each detection
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
		for barcode in barcodes:
			data = barcode.data.decode("utf-8")

			# Put barcode data on bounding box
			data_size, baseline = cv2.getTextSize(data, cv2.FONT_HERSHEY_SIMPLEX, 0.5, 1)
			cv2.rectangle(frame, (x1, y2), (x1 + data_size[0], y2 + data_size[1]), (0, 255, 0), cv2.FILLED)
			cv2.putText(frame, data, (x1, y2 + data_size[1]), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 0), 1)

			if data=='' :
				continue 

			try:
				# Replace these with your own database connection details
				host = "localhost"
				user = "root"
				password = ""
				database = "swiftcart"

				# Create a connection to the MySQL server
				conn = mysql.connector.connect(
						host=host,
						user=user,
						password=password,
						database=database
				)

				# Create a cursor object to interact with the database
				cursor = conn.cursor()

				# Get data from the request
				datad = dict() # request.get_json()
				datad['username']='vijay'
				datad['productcode'] =str(data)

				# SQL query for insertion
				insert_query = "INSERT INTO cart (username, productcode) VALUES (%s, %s)"

				# Execute the query with data
				cursor.execute(insert_query, (datad['username'], datad['productcode']))

				# Commit the changes
				conn.commit()

				# Close the cursor
				cursor.close()

				print('sql done') # jsonify({'success': True, 'message': 'Record inserted successfully'}))
				conn.close()
				import time
				time.sleep(1)
			except Exception as e:
					print('error ', e)

			finally:
					pass
					#conn.close()

	# Display annotated image
	cv2.imshow('Result', frame)
	if cv2.waitKey(1) & ord('q') == 27:
		cv2.destroyAllWindows()
		break
