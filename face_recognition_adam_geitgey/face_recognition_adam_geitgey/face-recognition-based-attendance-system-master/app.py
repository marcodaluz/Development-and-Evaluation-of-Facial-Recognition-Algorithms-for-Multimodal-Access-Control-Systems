import cv2
import os
import mysql.connector
from flask import Flask, request, render_template,redirect, url_for, Response
from datetime import date
from datetime import datetime
import numpy as np
from sklearn.neighbors import KNeighborsClassifier
import pandas as pd
import joblib
from flask import Flask, jsonify
from datetime import datetime
from werkzeug.utils import secure_filename
import face_recognition
import numpy as np
import json
from PIL import Image
import time
from werkzeug.utils import secure_filename
import cv2
import csv

# Defining Flask App
app = Flask(__name__)

# Number of images to take for each user
nimgs = 10

# Saving Date today in 2 different formats
datetoday = date.today().strftime("%m_%d_%y")


# Initializing VideoCapture object to access WebCam
face_detector = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')


# If these directories don't exist, create them
if not os.path.isdir('static'):
    os.makedirs('static')
if not os.path.isdir('static/faces'):
    os.makedirs('static/faces')

#################
def create_db_connection():
    return mysql.connector.connect(
        host="localhost",
        user="root",
        passwd="",
        database="project1"
    )

##função que vai buscar todos os users a base de dados
def get_all_users():
    connection = create_db_connection()
    cursor = connection.cursor()
    cursor.execute("SELECT * FROM Employee")
    users = cursor.fetchall()
    cursor.close()
    connection.close()
    return users

#####################################################################  save data in database ########################################################
# Function to save user data to database
def save_user_data(newuserid, newusername,password,cargo, departamento,HoraEntrada, HoraSaida, userpin):
    connection = create_db_connection()
    cursor = connection.cursor()
  
    sql_query = "INSERT INTO Employee (userid, username, password, cargo, departamento, HoraEntrada, HoraSaida, pin ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)"
    val = (newuserid, newusername,password,cargo, departamento,HoraEntrada, HoraSaida, userpin)
    cursor.execute(sql_query, val)
    connection.commit()
    cursor.close()
    connection.close()

#save entry attendance # estas duas funções, provavelmente vão sair
def save_attendance_to_db(userid, username, current_date, current_time):
    connection = create_db_connection()
    cursor = connection.cursor()
  
    sql_query = "INSERT INTO EntryRecord (userid, username, data, hora) VALUES (%s, %s, %s, %s)"
    val = (userid, username, current_date, current_time)
    cursor.execute(sql_query, val)
    connection.commit()

    cursor.close()
    connection.close()


#save exit attendance
def save_attendance_to_db_exit(userid, username, current_date, current_time):
    connection = create_db_connection()
    cursor = connection.cursor()

    sql_query = "INSERT INTO ExitRecord (userid, username, data, hora) VALUES (%s, %s, %s, %s)"
    val = (userid, username, current_date, current_time)
    cursor.execute(sql_query, val)
    connection.commit()

    cursor.close()
    connection.close()    

#############################################################################################################################
#############



################## ROUTING FUNCTIONS #########################

@app.route('/')
def home():
    
    return render_template('login.html')
@app.route('/loginSuccessful')
def loginSuccessful():
    return render_template('dashboard.html')

@app.route('/adduseradmin')
def adduseradmin():
    
    return render_template('adduseradmin.html')

@app.route('/dashboard')
def dashboard():
   
    return render_template('dashboard.html')

@app.route('/adduser')
def adduser():
    
    return render_template('adduser.html')

@app.route('/users')
def users():
    users_list = get_all_users()
    return render_template('users.html', users=users_list)

##################################################################### Routes for user admin ########################################################
@app.route('/addAdmin', methods=['POST'])
def addAdmin():
    username = request.form.get('username')
    password = request.form.get('password')
    position = request.form.get('position')    
    connection = create_db_connection()
    cursor = connection.cursor()
    
    if not username or not password:
       return "Error: Provide username and password in the request body.", 400
    
    comando = 'INSERT INTO useradmin (username,password,position) VALUES (%s, %s, %s)'
    valores = (username, password, position)
    cursor.execute(comando, valores)
    connection.commit()
    cursor.close()
    connection.close()
    return render_template('adduseradmin.html')


# get user admin
@app.route('/usersadmin', methods=['GET'])
def getUserAdmin():
    connection = create_db_connection()
    cursor = connection.cursor()
    command = 'SELECT id, username, password, position FROM useradmin'
    cursor.execute(command)
    usersdb = cursor.fetchall()
    users = [{'id': id, 'username': username, 'password': password, 'position': position} 
             for id, username, password, position in usersdb]
    return render_template('users_admin.html', users=users)

# Routes for admin login
@app.route('/login', methods=['POST'])
def login():
    connection = create_db_connection()
    cursor = connection.cursor()
    username = request.form.get('username')
    password = request.form.get('password')
    print("Username:", request.form.get('username'))
    print("Password:", request.form.get('password'))
    
    if not username or not password:
        return "Error: Provide username and password in the request body", 400
    
    command = 'SELECT * FROM useradmin WHERE username = %s AND password = %s'
    values = (username, password)
    cursor.execute(command, values)
    utilizador = cursor.fetchone()
    if utilizador:
        return redirect(url_for('loginSuccessful'))
    else:
        return render_template('login.html')



# Recognition functionality. 
#####################################################################  adicionar e reconhecimentos rotas ########################################################



@app.route('/add', methods=['POST'])
def add():

    connection = create_db_connection()
    cursor = connection.cursor()

    newusername = request.form['newusername']
    newuserid = request.form['newuserid']
    password = request.form['password']
    cargo = request.form['cargo']
    departamento = request.form['departamento']
    HoraEntrada = request.form['horaentrada']
    HoraSaida = request.form['horasaida']
    userpin = request.form['pin']


    save_user_data(newuserid, newusername, password, cargo, departamento, HoraEntrada, HoraSaida, userpin )

    user_image_folder = 'static/faces/' + newusername + '_' + str(newuserid)
    if not os.path.isdir(user_image_folder):
        os.makedirs(user_image_folder)

    if 'photo' in request.files:
        photo = request.files['photo']
        if photo.filename != '':
        
            filename = secure_filename(photo.filename)
            photo.save(os.path.join(user_image_folder, filename))

         
            img = face_recognition.load_image_file(os.path.join(user_image_folder, filename))
            img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)

            face_locations = face_recognition.face_locations(img)
            if face_locations:
                photo_code = face_recognition.face_encodings(img, face_locations)[0]

                photo_code_str = ','.join(map(str, photo_code.tolist()))

                insert_query = "INSERT INTO photo (photo_code, user_id) VALUES (%s, %s)"
                photo_data = (photo_code_str, newuserid)
                cursor.execute(insert_query, photo_data)
                connection.commit()

                
                csv_filename = "userdata.csv"
                file_exists = os.path.exists(csv_filename)
                with open(csv_filename, 'a', newline='') as file:
                    writer = csv.writer(file)
                    if not file_exists:
                        writer.writerow(['photo_code', 'user_id', 'newusername'])  
                    writer.writerow([photo_code_str, newuserid,newusername])

    cursor.close()
    connection.close()
    return render_template('adduser.html')

def gen_frames(camera):
    while True:
        success, frame = camera.read()
        if not success:
            break
        else:
            ret, buffer = cv2.imencode('.jpg', frame)
            frame = buffer.tobytes()
            yield (b'--frame\r\n'
                   b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')

@app.route('/video_feed')
def video_feed():
    return Response(gen_frames(cv2.VideoCapture(0)),
                    mimetype='multipart/x-mixed-replace; boundary=frame')
#####Recognition function
@app.route('/capture_and_process', methods=['POST'])
def capture_and_process():
    if 'image' not in request.files:
        return jsonify(error="Nenhum arquivo de imagem foi recebido"), 400
    image = request.files['image']
    if image.filename == '':
        return jsonify(error="Nome de arquivo vazio"), 400

    filestr = image.read()
    if not filestr:
        return jsonify(error="O arquivo está vazio"), 400

    npimg = np.frombuffer(filestr, np.uint8)
    frame = cv2.imdecode(npimg, cv2.IMREAD_COLOR)

    face_locations = face_recognition.face_locations(frame)
    face_encodings = face_recognition.face_encodings(frame, face_locations)

    if not face_encodings:
        return jsonify(error="Nenhum encoding gerado"), 404

    userdata = pd.read_csv('userdata.csv')
    known_encodings = [np.fromstring(encoding, sep=',') for encoding in userdata['photo_code']]
    known_user_ids = userdata['user_id'].tolist()
    known_usernames = userdata['newusername'].tolist()  

    results = []
    for location, encoding in zip(face_locations, face_encodings):
        distances = face_recognition.face_distance(known_encodings, encoding)
        best_match_index = np.argmin(distances) if len(distances) else None
        if best_match_index is not None and distances[best_match_index] <= 0.6:
            user_id = known_user_ids[best_match_index]
            username = known_usernames[best_match_index] 
            results.append({"user_id": user_id, "username": username, "distance": distances[best_match_index], "location": location})
            current_date = datetime.now().strftime("%Y-%m-%d")  
            current_time = datetime.now().strftime("%H:%M:%S") 
            save_attendance_to_db(user_id, username, current_date, current_time)
            print(f"Employee identified with the ID: {user_id}, Username: {username}, Distance: {distances[best_match_index]}")
        else:
            results.append({"user_id": None, "username": None, "distance": None, "location": location})
            print("Nenhuma face conhecida correspondente encontrada.")

    return jsonify(result="Processamento concluído", faces=results)




@app.route('/markAttendanceEntry')
def index():
    return render_template('cap.html')


@app.route('/capture_and_process2', methods=['POST'])
def capture_and_process2():
    if 'image' not in request.files:
        return jsonify(error="Nenhum arquivo de imagem foi recebido"), 400
    image = request.files['image']
    if image.filename == '':
        return jsonify(error="Nome de arquivo vazio"), 400

    filestr = image.read()
    if not filestr:
        return jsonify(error="O arquivo está vazio"), 400

    npimg = np.frombuffer(filestr, np.uint8)
    frame = cv2.imdecode(npimg, cv2.IMREAD_COLOR)

    face_locations = face_recognition.face_locations(frame)
    face_encodings = face_recognition.face_encodings(frame, face_locations)

    if not face_encodings:
        return jsonify(error="Nenhum encoding gerado"), 404

    userdata = pd.read_csv('userdata.csv')
    known_encodings = [np.fromstring(encoding, sep=',') for encoding in userdata['photo_code']]
    known_user_ids = userdata['user_id'].tolist()
    known_usernames = userdata['newusername'].tolist()  

    results = []
    for location, encoding in zip(face_locations, face_encodings):
        distances = face_recognition.face_distance(known_encodings, encoding)
        best_match_index = np.argmin(distances) if len(distances) else None
        if best_match_index is not None and distances[best_match_index] <= 0.6:
            user_id = known_user_ids[best_match_index]
            username = known_usernames[best_match_index] 
            results.append({"user_id": user_id, "username": username, "distance": distances[best_match_index], "location": location})
            current_date = datetime.now().strftime("%Y-%m-%d")  
            current_time = datetime.now().strftime("%H:%M:%S") 
            save_attendance_to_db_exit(user_id, username, current_date, current_time)
            print(f"Employee identified with the ID: {user_id}, Username: {username}, Distance: {distances[best_match_index]}")
        else:
            results.append({"user_id": None, "username": None, "distance": None, "location": location})
            print("Nenhuma face conhecida correspondente encontrada.")

    return jsonify(result="Processamento concluído", faces=results)



@app.route('/markAttendanceExit')
def index2():
    return render_template('cap2.html')




################################# rooms function ###################################
@app.route('/insertRoom')
def insertRoom():
    return render_template('addroom.html')

@app.route('/listRoom')
def listRoom():
    return render_template('listrooms.html')

@app.route('/listRoomAccess')
def list_room_access():
    connection = create_db_connection()
    cursor = connection.cursor()
    cursor.execute("""
        SELECT ra.userid, ra.username, ra.roomNumber, ra.data, ra.hora, ra.status
        FROM roomacess ra
        ORDER BY ra.data DESC, ra.hora DESC
    """)
    accesses = cursor.fetchall()
    cursor.close()
    connection.close()
    return render_template('roomsacess.html', accesses=accesses)

@app.route('/addRoom', methods=['POST'])
def add_room():
   
    connection = create_db_connection()
    cursor = connection.cursor()

   
    room_number = request.form.get('roomNumber')
    description = request.form.get('description')
    access_level = request.form.get('acesslevel')#.lower() == 'true'

    if not room_number.isdigit():
        return "Error: Room Number must be an integer.", 400

    # Prepara a consulta SQL para inserir os dados na tabela room
    sql_query = 'INSERT INTO room (roomNumber, description, accessLevel) VALUES (%s, %s, %s)'
    values = (int(room_number), description, access_level)

    try:
        cursor.execute(sql_query, values)
        connection.commit()
    except mysql.connector.Error as err:
    
        return "MySQL error: {}".format(err), 500
    finally:
        
        cursor.close()
        connection.close()
    return redirect(url_for('listRoom'))

@app.route('/listRooms')
def list_rooms():
   
    connection = create_db_connection()
    cursor = connection.cursor()  
    query = "SELECT roomNumber, description, accessLevel FROM room"
    cursor.execute(query)
    rooms = cursor.fetchall()  # Fetch all results
    cursor.close()
    connection.close()
    return render_template('listrooms.html', rooms=rooms)



#####################################################################  presencas routes ########################################################
@app.route('/entryRecord') 
def entryRecord():
    connection = create_db_connection()
    cursor = connection.cursor()
    cursor.execute("""
        SELECT p.id, p.userid, p.username, p.data, p.hora, u.HoraEntrada 
        FROM EntryRecord p
        LEFT JOIN Employee u ON p.userid = u.userid  
        ORDER BY p.data DESC, p.hora DESC
    """)
    attendances = cursor.fetchall()
    cursor.close()
    connection.close()
    return render_template('entryRecords.html', attendances=attendances)

@app.route('/exitRecord')
def exitRecord():
    connection = create_db_connection()
    cursor = connection.cursor()
    cursor.execute("""
        SELECT s.id, s.userid, s.username, s.data, s.hora, u.HoraSaida 
        FROM ExitRecord s
        LEFT JOIN Employee u ON s.userid = u.userid  
        ORDER BY s.data DESC, s.hora DESC
    """)
    attendances = cursor.fetchall()
    cursor.close()
    connection.close()
    return render_template('eixtRecords.html', attendances=attendances)

#####################################################################  mobile routs ########################################################
@app.route('/attendance/<int:userid>', methods=['GET'])
def get_user_attendance(userid):
    connection = create_db_connection()
    cursor = connection.cursor()
    cursor.execute("SELECT * FROM EntryRecord WHERE userid = %s", (userid,))
    user_attendance = cursor.fetchall()
    cursor.close()
    connection.close()

    # Converter os resultados em um formato JSON
    attendance_json = []
    for attendance in user_attendance:
        attendance_dict = {
            "userid": attendance[1],
            "username": attendance[2],
            "data": attendance[3].strftime("%Y-%m-%d"),
            "hora": str(attendance[4])  # Converter timedelta para string
        }
        attendance_json.append(attendance_dict)
     
    return jsonify(attendance_json)
#--#
@app.route('/outs/<int:userid>', methods=['GET'])
def get_user_out(userid):
    connection = create_db_connection()
    cursor = connection.cursor()
    cursor.execute("SELECT * FROM ExitRecord WHERE userid = %s", (userid,))
    user_attendance = cursor.fetchall()
    cursor.close()
    connection.close()

    # Converter os resultados em um formato JSON
    attendance_json = []
    for attendance in user_attendance:
        attendance_dict = {
            "userid": attendance[1],
            "username": attendance[2],
            "data": attendance[3].strftime("%Y-%m-%d"),
            "hora": str(attendance[4])  # Converter timedelta para string
        }
        attendance_json.append(attendance_dict)
     
    return jsonify(attendance_json)

@app.route('/RoomAcess/<int:userid>', methods=['GET'])
def get_user_RoomAcess(userid):
    connection = create_db_connection()
    cursor = connection.cursor()
    cursor.execute("SELECT * FROM RoomAcess WHERE userid = %s", (userid,))
    user_attendance = cursor.fetchall()
    cursor.close()
    connection.close()

    # Converter os resultados em um formato JSON
    attendance_json = []
    for attendance in user_attendance:
        attendance_dict = {
            "userid": attendance[1],
            "username": attendance[2],
            "data": attendance[3].strftime("%Y-%m-%d"),
            "hora": str(attendance[4]),  # Converter timedelta para string
            "roomNumber": attendance[5],
            "status": str(attendance[6])

        }
        attendance_json.append(attendance_dict)

    return jsonify(attendance_json)
#--#
@app.route('/loginapp', methods=['POST'])
def api_login():
    if request.method == 'POST':
        data = request.get_json()

        if 'username' in data and 'password' in data:
            username = data['username']
            password = data['password']

            # Query the database to check login credentials
            connection = create_db_connection()
            cursor = connection.cursor()
            cursor.execute("SELECT * FROM Employee WHERE username = %s AND password = %s", (username, password))
            user = cursor.fetchone()
            cursor.close()
            connection.close()

            if user:
                # User found, return user information (excluding password)
                user_info = {
                    'userid': user[0],
                    'username': user[1],
                    'cargo': user[3],
                    'departamento': user[4],
                    'HoraEntrada': str(user[5]),
                    'HoraSaida': str(user[6])
                }
                return jsonify(user_info), 200
            else:
                # Invalid credentials
                return jsonify({'error': 'Invalid username or password'}), 401

        else:
            return jsonify({'error': 'Missing username or password in the request'}), 400
        
#####rasberry pi route
@app.route('/receivenumber', methods=['POST'])
def receive_number():
    data = request.get_json()
    number = data.get('number')
    room_number = data.get('roomNumber')

    if not number or not room_number:
        return jsonify({'result': False, 'message': 'Número ou número do quarto faltando'})

    try:
        connection = create_db_connection()
        cursor = connection.cursor()
        
       
        sql = "SELECT userid, username FROM employee WHERE pin = %s"
        cursor.execute(sql, (number,))
        result = cursor.fetchone()
        
        if not result:
            return jsonify({'result': False, 'message': 'PIN não encontrado'})

        userid = result[0]  # Accessing by index instead of key
        username = result[1]  # Accessing by index instead of key

        # Marca presença na tabela RoomAccess
        sql = """
        INSERT INTO roomacess (userid, username, data, hora, roomNumber, status)
        VALUES (%s, %s, CURDATE(), CURTIME(), %s, 1)
        """
        cursor.execute(sql, (userid, username, room_number))
        connection.commit()

        return jsonify(result=True), 200
    except Exception as e:
        print(f"Erro: {e}")
        return jsonify(result=False), 200
    finally:
        cursor.close()
        connection.close()

if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=False)
    