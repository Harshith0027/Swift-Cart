# import pandas as pd

# items = pd.read_csv(r"C:\Users\bitra\OneDrive\Desktop\IV_Project\Files\Items Database - Sheet1.csv")
# result = items[items['Barcode'] == "ABC-abc-1234"]
# print(result.iloc[0]['img_Url'])

import torch 
device = torch.device('cuda:0' if torch.cuda.is_available() else 'cpu')
if torch.cuda.is_available():
  print("true")
else:
  print("false")