import face_recognition
import pickle
import numpy as np
import threading
from PIL import Image
import io
import base64

ENCODINGS_PATH = "encodings.pkl" 
TOLERANCE = 0.6  # makin kecil makin ketat pengenalan

class FaceRecognizer:
    def __init__(self):
        self._lock = threading.Lock()
        self._data = self._load_encodings()

    def _load_encodings(self):
        try:
            with open(ENCODINGS_PATH, "rb") as f:
                return pickle.load(f)
        except FileNotFoundError:
            return {"names": [], "encodings": []}

    def _base64_to_image(self, base64_str: str):
        try:
            img_bytes = base64.b64decode(base64_str)
            img = Image.open(io.BytesIO(img_bytes))
            img = img.resize((320, 240))
            return np.array(img)
        except Exception:
            return None

    def recognize(self, base64_image: str):
        with self._lock:
            img_array = self._base64_to_image(base64_image)
            
            if img_array is None:
                return {"status": "unknown", "name": None, "confidence": 0.0}
            
            face_locations = face_recognition.face_locations(img_array)

            if not face_locations:
                return {"status": "unknown", "name": None, "confidence": 0.0}

            face_encodings = face_recognition.face_encodings(img_array, face_locations)

            for face_encoding in face_encodings:
                distances = face_recognition.face_distance(
                    self._data["encodings"], face_encoding
                )

                if len(distances) == 0:
                    continue

                best_match_index = np.argmin(distances)
                best_distance = distances[best_match_index]
                confidence = round(1 - float(best_distance), 2)

                if best_distance <= TOLERANCE:
                    name = self._data["names"][best_match_index]
                    return {"status": "recognized", "name": name, "confidence": confidence}

            return {"status": "unknown", "name": None, "confidence": round(confidence, 2)}

    def enroll(self, name: str, base64_image: str):
        with self._lock:
            img_array = self._base64_to_image(base64_image)
            face_locations = face_recognition.face_locations(img_array)

            if not face_locations:
                return {"status": "error", "message": "Tidak ada wajah terdeteksi"}

            face_encoding = face_recognition.face_encodings(img_array, face_locations)[0]

            self._data["names"].append(name)
            self._data["encodings"].append(face_encoding)

            with open(ENCODINGS_PATH, "wb") as f:
                pickle.dump(self._data, f)

            return {"status": "enrolled", "name": name}