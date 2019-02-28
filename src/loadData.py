
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
    datos = dataframe.loc[dataframe['SS']>=400]
    
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
    Param seconds: seconds of seizures or moment when seizure ends"""
    startDate = pd.to_datetime(datetime)
    
    if isinstance(seconds,int): 
        endDate = startDate + pd.to_timedelta(seconds, unit='s')
    else: 
        endDate = pd.to_datetime(seconds)   
            
    mask = (dataframe['DateTime'] > startDate) & (dataframe['DateTime'] <= endDate)
    dataframe.loc[mask,'target']=True
    
    return dataframe


def segmentNights(datosLimpios): 
    inicios = list()
    finales = list()

    anterior = pd.to_datetime('1970-01-01 00:00:00')
    margen = pd.to_timedelta(1,unit='h')
    for index,dl in datosLimpios.iterrows():
        hora = dl['DateTime']
        diff = hora-anterior
        if diff >= margen:
            if len(inicios)>len(finales): #Primera vuelta no introduce la fecha anterior
                finales.append(anterior)
            inicios.append(hora)
        anterior = hora
 
    finales.append(pd.Timestamp.now())
    
    trozos = []
    for i in range(len(inicios)):
        ini = inicios[i]
        fin = finales[i]
        mask = (datosLimpios['DateTime'] >= ini) & (datosLimpios['DateTime'] <= fin)
        trozos.append(datosLimpios.loc[mask])
        
    return trozos
