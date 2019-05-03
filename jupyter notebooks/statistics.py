import pandas as pd

def calculateMean(data, window):
    """
    Calcula la media móvil de data con ventana = window
    Recalcula los target en función de la ventana 
    Devuelve las medias móviles
    """
    
    rollMean = data.copy()
    
    columns = len(data.columns)-1
    
    #calcular media móvil
    for i in range(1,columns): 
        name_col = 'P'+str(i)
        name_col_mean = name_col+' mean'
        rollMean[name_col_mean] = rollMean[name_col].rolling(window).mean()
    
    #recalcular target en función de la ventana 
    indexes =[]
        
    seizures = rollMean.loc[rollMean['target'] == True]
    for index, row in seizures.iterrows(): 
        if rollMean.loc[index-1, 'target'] == False: 
            indexes.append(index)
     
    for i in indexes: 
        rollMean.loc[i:i+window-1, 'target'] = False 
    
    #eliminar columnas de los datos iniciales y filas nulas 
    for i in range(1,columns): 
        del rollMean['P'+str(i)]
        
    rollMean = rollMean.dropna()
    
    return rollMean

def calculateStd(data, window): 
    """
    Calcula la desviación móvil de data con ventana = window
    Recalcula los target en función de la ventana 
    Devuelve las desviaciones móviles
    """
    rollStd = data.copy()
    
    columns = len(data.columns)-1
    
    #calcular desviación móvil 
    for i in range(1,columns): 
        name_col = 'P'+str(i)
        name_col_std = name_col+' std'
        rollStd[name_col_std] = rollStd[name_col].rolling(window).std()
    
    #recalcular target en función de la ventana
    indexes = []
    
    seizures = rollStd.loc[rollStd['target'] == True]
    for index, row in seizures.iterrows(): 
        if rollStd.loc[index-1, 'target'] == False: 
            indexes.append(index)
            
    for i in indexes: 
        rollStd.loc[i:i+window-1, 'target'] = False
    
    #eliminar columnas de los datos iniciales y filas nulas
    for i in range(1, columns): 
        del rollStd['P'+str(i)]
        
    rollStd = rollStd.dropna()
    
    return rollStd
    
        