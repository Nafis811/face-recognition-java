from fastapi import FastAPI
from pydantic import BaseModel
from recognizer import FaceRecognizer

app = FastAPI()
recognizer = FaceRecognizer()

class RecognizeRequest(BaseModel):
    image: str

class EnrollRequest(BaseModel):
    name: str
    image: str

@app.post("/recognize")
def recognize(req: RecognizeRequest):
    return recognizer.recognize(req.image)


@app.post("/enroll")
def enroll(req: EnrollRequest):
    return recognizer.enroll(req.name, req.image)

