import face_recognition
import pickle
import os
import numpy as np

from collections import Counter
from PIL import Image, ExifTags

DATASET_PATH = "dataset"
ENCODINGS_PATH = "encodings.pkl"

def fix_exif_rotation(filepath):
    img = Image.open(filepath)
    try:
        for orientation in ExifTags.TAGS.keys():
            if ExifTags.TAGS[orientation] == 'Orientation':
                break
        exif = img._getexif()
        if exif and orientation in exif:
            if exif[orientation] == 3:
                img = img.rotate(180, expand=True)
            elif exif[orientation] == 6:
                img = img.rotate(270, expand=True)
            elif exif[orientation] == 8:
                img = img.rotate(90, expand=True)
    except Exception:
        pass
    return np.array(img)[:, :, :3]

data = {"names": [], "encodings": []}

for name in os.listdir(DATASET_PATH):
    person_folder = os.path.join(DATASET_PATH, name)
    
    if not os.path.isdir(person_folder):
        continue
    
    print(f"Encoding wajah: {name}")
    
    for filename in os.listdir(person_folder):
        filepath = os.path.join(person_folder, filename)
        
        image = fix_exif_rotation(filepath)
        locations = face_recognition.face_locations(image)
        encodings = face_recognition.face_encodings(image, locations)
        
        if len(encodings) == 0:
            print(f"  WARNING: Tidak ada wajah terdeteksi di {filename}, skip.")
            continue
        
        data["names"].append(name)
        data["encodings"].append(encodings[0])
        print(f"  OK: {filename}")

with open(ENCODINGS_PATH, "wb") as f:
    pickle.dump(data, f)

with open("encodings.pkl", "rb") as f:
    data = pickle.load(f)

print(f"\nSelesai. Total encoding: {len(data['names'])}")
print(Counter(data["names"]))
print(f"Total: {len(data['names'])}")