"""
███████╗███╗   ███╗ ██████╗ ████████╗██╗ ██████╗ ███╗   ██╗    ██████╗     ██████╗
██╔════╝████╗ ████║██╔═══██╗╚══██╔══╝██║██╔═══██╗████╗  ██║    ╚════██╗   ██╔═████╗
█████╗  ██╔████╔██║██║   ██║   ██║   ██║██║   ██║██╔██╗ ██║     █████╔╝   ██║██╔██║
██╔══╝  ██║╚██╔╝██║██║   ██║   ██║   ██║██║   ██║██║╚██╗██║    ██╔═══╝    ████╔╝██║
███████╗██║ ╚═╝ ██║╚██████╔╝   ██║   ██║╚██████╔╝██║ ╚████║    ███████╗██╗╚██████╔╝
╚══════╝╚═╝     ╚═╝ ╚═════╝    ╚═╝   ╚═╝ ╚═════╝ ╚═╝  ╚═══╝    ╚══════╝╚═╝ ╚═════╝

emotIOn 2.0
By: Andres Rico - Visitng Researcher @ MIT Media Lab's City Science Group.

This file contains the main functions needed to conduct experiments with the emotIOn project. The file contains a Neural Network that can
be trained for individual users with training data obtained by using the emotIOn app, available from the Google Play Store.

This file can also be used for online predictions with trained models. Uncomment sections to activate fucntionalities.

"""


import tensorflow as tf                                                         #Import needed libraries
from tensorflow import keras                                                    #Machine learning
import numpy as np                                                              #Array handling
import matplotlib.pyplot as plt                                                 #Plotting
import socket                                                                   #UDP Communication
import time
import re

#this section is only used for live predicting sessions. UDP communication with terMITe is established.
#UDP_IP = "192.168.0.23" #Specify IP Address for communication.
#UDP_PORT = 19990 #Specify UDP Communication port

#sock = socket.socket(socket.AF_INET, # Internet
#                     socket.SOCK_DGRAM) # UDP
#sock.bind((UDP_IP, UDP_PORT))

a = np.loadtxt(open("Insert Specific Data File Name Here"), delimiter = ",", skiprows = 1) #Take Data from file on src path.

for i in range(1): #Range 50
    np.random.shuffle(a) #Shuffle Data Set

index = int(round(a.shape[0] * .7)) #Divide set into training (70%) and test (30%)

Y = a[0:index,10] #Create Y label vector for training.
Y = Y - 1 #Adjust Y label vector values to fit NN.
X = a[0:index,0:6] #Create X atrix for training.
Xtest = a[(index + 1):a.shape[0],0:6] #Create X matrix for testing.
Ytest = a[(index + 1):a.shape[0],10] #Create Y vector for testing.
Ytest = Ytest - 1 #Adjust values of Y vector.

class_names = ['N', 'HH', 'LH', 'LL', 'HL'] #label to know different classes of affetive states within circumplex model of affect.

model = keras.Sequential([ #Declare a secuential Feed Forward Neural Network With Keras.

    keras.layers.Dense(200,input_dim = 6 , activation = 'relu'), #input layer for the model. Takes input with six variables coming from terMITe. Adjust input_dim to add more sensors.
    #Hidden layers sequence. Each layer has 200 neurons with activation fucntion relu on every one of them .
    keras.layers.Dense(200, activation=tf.nn.relu, use_bias=True, kernel_initializer='glorot_uniform', bias_initializer='zeros', kernel_regularizer=None, bias_regularizer=None, activity_regularizer=None, kernel_constraint=None, bias_constraint=None),
    keras.layers.Dense(200, activation=tf.nn.relu, use_bias=True, kernel_initializer='glorot_uniform', bias_initializer='zeros', kernel_regularizer=None, bias_regularizer=None, activity_regularizer=None, kernel_constraint=None, bias_constraint=None),
    keras.layers.Dense(200, activation=tf.nn.relu, use_bias=True, kernel_initializer='glorot_uniform', bias_initializer='zeros', kernel_regularizer=None, bias_regularizer=None, activity_regularizer=None, kernel_constraint=None, bias_constraint=None),
    #Output layer has 5 neurons for each one of the five affective states. Output vector contains probabilities of classification.
    keras.layers.Dense(5, activation=tf.nn.softmax, use_bias=True, kernel_initializer='glorot_uniform', bias_initializer='zeros', kernel_regularizer=None, bias_regularizer=None, activity_regularizer=None, kernel_constraint=None, bias_constraint=None)

])

model.compile(optimizer='rmsprop', #Uses root mean squared error for optimization.
              loss='sparse_categorical_crossentropy', # soarse categorical cross entropy is used as loss function.
              metrics=['accuracy'])

history = model.fit(X, Y, validation_split = 0.33, batch_size = 500, epochs=1500) #Epochs 60, 1000 Training can be done with different combinations of epochs depending on the data set used.
print(history.history.keys()) #terminal outout of accuracy results.

test_loss, test_acc = model.evaluate(Xtest, Ytest) #Evaluate model with test sets (X and Y).

print('Test accuracy:', test_acc) #Terminal print of final accuracy of model.

predictions = model.predict(Xtest) #Uses test set to predict.

model.summary()
model.get_config()
print ('Number of Training Examples Used: ' , Y.size) #Helps get number of training examples used.
print ('Hours of Data;' , (Y.size * 1.5) / 3600) #Calculates hours of data. Intervals of 1.5 seconds are used to obtain data.

#plt.style.use('dark_background')

#Complete sript for plotting end results for accuracy on test and training set across different epochs.
plt.rcParams.update({'font.size': 25})
plt.figure(1)
plt.plot(history.history['acc'], '-') #Plot Accuracy Curve
plt.plot(history.history['val_acc'], ':')
plt.title('Model Accuracy U6')
plt.ylabel('Accuracy')
plt.xlabel('Epoch')
plt.legend(['Training Set', 'Test Set'], loc='lower right')
plt.show()

"""
The following functions are for plotting different resuts from the model.

#plt.figure(2)
#plt.plot(history.history['loss']) #Plot Loss Curvecompletedata = []
#plt.title('Model Loss')
#plt.ylabel('Loss')
#plt.xlabel('Epoch')
#plt.legend(['Test Set'], loc='upper left')
#plt.show()

#plt.figure(3)
#for times in range(100):
#    if np.argmax(predictions[times]) == Ytest[times]:
#        plt.plot(times, (Y[times]), 'go')
#    else:
#        plt.plot(times, np.argmax(predictions[times]), 'rx')
#        plt.plot(times, ((Ytest[times])), 'bo')
#    plt.axis([0, 100, -1, 5])
#    plt.title('Prediction Space')
    #plt.legend()
#plt.show()

#while True:
#    data, addr = sock.recvfrom(1024) # buffer size is 1024 bytes
#    indata = np.fromstring(data, dtype = float, sep = ',')
#    indata = indata[0:5]
#    indata = np.expand_dims(indata, 0)
#    prediction = model.predict(indata)
#    print class_names[np.argmax(prediction[0])]
#    time.sleep(60)

"""
