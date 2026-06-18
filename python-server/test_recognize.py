import base64
import requests

# Ganti dengan path foto salah satu anggota yang ada di dataset
IMAGE_PATH = r"C:\Users\madi\face-recognition-java\python-server\WhatsApp Image 2026-06-18 at 8.44.55 PM.jpeg"

with open(IMAGE_PATH, "rb") as f:
    image_base64 = base64.b64encode(f.read()).decode("utf-8")

response = requests.post(
    "http://localhost:8000/recognize",
    json={"image": image_base64}
)

print(response.json())