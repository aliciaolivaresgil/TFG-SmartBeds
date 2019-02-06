from sklearn.base import TransformerMixin
import numpy as np
import pandas as pd

class Normalizer(TransformerMixin):
    """
    Normalize all data between 0 and 1. 
    """
    
    def fit(self, data, y=None):
        return self

    def transform(self, data):
        maxi = np.max(np.max(data))
        mini = np.min(np.min(data))
        rang = maxi-mini   
        dataNorm = (data - mini) / rang
        return dataNorm

class NoiseFilter(TransformerMixin): 
    """
    Makes 0 all values less than 'minimum'. 
    """
    def fit(self, data, minimum=0):
        self._minimum = minimum 
        return self
    
    def transform(self, data):
        for d in data: 
            if data[d].dtype == np.float64: 
                data.loc[data[d]< self._minimum, d]=0.0      
        return data
    
class StatisticsTransformer(TransformerMixin): 
    """
    Calculates rolling statistics of the columns from data. 
    
    Parameters: 
    
        mode: string, default 'mean'
            'mean': rolling mean
            'std': rolling standard deviation
            'range' : rolling ranges (difference between max and min)
            
        window: int, dafault 25. 
    """
    
    def fit(self, data, mode='mean', window=25): 
        self._mode = mode
        self._window = window
        return self
    
    def transform(self, data):
        statistics = pd.DataFrame()
        for c in data.columns: 
            if self._mode == 'mean': 
                statistics[c+' '+self._mode] = data[c].rolling(self._window).mean()
            elif self._mode == 'std': 
                statistics[c+' '+self._mode] = data[c].rolling(self._window).std()
            elif self._mode == 'range': 
                statistics[c+' '+self._mode] = data[c].rolling(self._window).max() - data[c].rolling(self._window).min()
            else: 
                raise Exception("mode: '"+self._mode+"' is not correct. Aviable modes are 'mean', 'std' and 'range'.")
        return statistics
    
                    
            
        