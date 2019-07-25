# -*- coding: utf-8 -*-
import base64
import re
from io import BytesIO
from PIL import Image
import PIL, sys

def cropImage(image):
    center_x = 0
    center_y = 100
    half_width = 84
    half_height = 100
    image_data = re.sub('^data:image/.+;base64,', '', image)
    imageObject = Image.open(BytesIO(base64.b64decode(image_data)))
    cropped = imageObject.crop((center_x,
                                center_y - half_height,
                                half_width,
                                half_height,
                                ))
    in_mem_file = BytesIO()
    cropped.save(in_mem_file, format="PNG")
    in_mem_file.seek(0)
    img_bytes = in_mem_file.read()
    base64_encoded_result_bytes = base64.b64encode(img_bytes)
    base64_encoded_result_str = base64_encoded_result_bytes.decode('ascii')
    in_mem_file.close()
    return base64_encoded_result_str
