
"""Read and preprocess data"""
import pandas as pd
import glob
import numpy as np

def load(path=r"./"):
    """Read all CSV in $folder to same DataFrame
    Param path: folder in regular expresion
    Return: Pandas DataFrame
    """
    allFiles = glob.glob(path + "/*.csv")

    list_ = []

    for file_ in allFiles:
        df = pd.read_csv(file_,index_col=None, header=0)
        list_.append(df)

    return pd.concat(list_, axis = 0, ignore_index = True)       

def preprocess(dataframe):
    """Only Datetime and preasures, add target
       Param: dataframe with all data
       Return: new datafram"""
    datos = dataframe.iloc[:,2:16]
    datos = datos.dropna()
    
    tam = len(datos)
    datos['target'] = np.zeros(tam,dtype='bool')

    date = datos['Date']
    time = datos['Time']
    datetime = date+" "+time
    datos['DateTime'] = pd.to_datetime(datetime)

    datos = datos[["DateTime","P1","P2","P3","P4","P5","P6","P7","P8","P9","P10","P11","P12","target"]]
    
    datos = datos.sort_values(by='DateTime')
    
    return datos
    

def newSeizure(dataframe,datetime,seconds):
    """Put true target in datetime to seconds
    Param dataframe: dataframe to change to true
    Param datetime: moment when seizure start
    Param seconds: seconds of seizures"""
    startDate = pd.to_datetime(datetime)
    endDate = startDate + pd.to_timedelta(seconds, unit='s')

    mask = (dataframe['DateTime'] > startDate) & (dataframe['DateTime'] <= endDate)
    dataframe.loc[mask,'target']=True
    
    return dataframe

def normalize(data): 
    maxi = np.max(data)
    mini = np.min(data)
    rang = maxi-mini   
    dataNorm = (data - mini) / rang
    return dataNorm
    