from sklearn.base import TransformerMixin
from sklearn.feature_selection import VarianceThreshold
from scipy import signal as sg
from pandas import DataFrame
import numpy as np
import pandas as pd

class FilterTransformer(TransformerMixin):

    def transform(self,X):
        Y = X.copy()
        size = len(Y.columns)
        #if isinstance(X,DataFrame):
        #    X = X.to_numpy()
        for f in X.columns:
            y = self._filData(X[f])
            Y[f+" "+self._NAME] = y
        return Y.iloc[:,size:len(Y.columns)]
    
    def _filData(self,X):
        pass

class ButterTransformer(FilterTransformer):

    def __init__(self, N, Wn,btype='low', analog=False, output='ba', fs=None):
        self._NAME = 'BUTTER'
        self._N = N
        self._Wn = Wn
        self._btype = btype
        self._analog = analog
        self._output = output
        #self._fs = fs

    def fit(self, X, y=None):
        self._b, self._a = sg.butter(self._N,self._Wn,btype=self._btype,analog=self._analog,output=self._output)
        return self

    def _filData(self,X):
        return sg.filtfilt(self._b,self._a,X)


class SavgolTransformer(FilterTransformer):

    def __init__(self, window_length, polyorder=2, deriv=0, delta=1.0, axis=-1, mode='interp', cval=0.0):
        self._NAME = 'SAVGOL'
        self._window_length = window_length
        self._polyorder = polyorder
        self._deriv = deriv
        self._delta = delta
        self._axis = axis
        self._mode = mode
        self._cval = cval

    def fit(self, X, y=None):
        return self

    def _filData(self,X):
        return sg.savgol_filter(X,self._window_length,self._polyorder,deriv=self._deriv,delta=self._delta,axis=self._axis,mode=self._mode,cval=self._cval)
    
class VarianceThresholdPD(TransformerMixin):
    
    def __init__(self,threshold=0.0):
        self._threshold = threshold
        
    def fit(self, X, y=None):
        return self
    
    def transform(self,X):
        data = X.copy()
        selector = VarianceThreshold(threshold=self._threshold)
        selector.fit(data)
        return data[data.columns[selector.get_support(indices=True)]]

###Compuestos###

class ConcatenateTransformer(TransformerMixin):

    def __init__(self,*transformers):
        self._transformers = transformers

    def fit(self,X,y=None):
        return self

    def transform(self,X):
        Y = DataFrame()
        for t in self._transformers:
            r = t.fit_transform(X)
            Y = pd.concat([Y,r],axis=1)
        return Y

class PipelineTransformer(TransformerMixin):

    def __init__(self,*transformers):
        self.transformers = transformers

    def fit(self,X,y=None):
        return self

    def transform(self,X):
        Y = X.dropna()
        for t in self.transformers:
            Y = t.fit_transform(Y).dropna()
        return Y
    
class Normalizer(TransformerMixin):
    """
    Normalize all data between 0 and 1. 
    """
    def __init__(self,max_=1):
        self._max = max_
    
    def fit(self, X, y=None):
        return self

    def transform(self, data):
        maxi = np.max(np.max(data))
        mini = np.min(np.min(data))
        rang = maxi-mini   
        dataNorm = (data - mini) / rang
        return dataNorm*self._max

class NoiseFilter(TransformerMixin): 
    """
    Makes 0 all values less than 'minimum'. 
    """
    def __init__(self,minimum=0):
        self._minimum = minimum
    
    def fit(self,X,y=None):
        return self
    
    def transform(self, data):
        dataN = data.copy()
        for d in dataN: 
            if dataN[d].dtype == np.float64: 
                dataN.loc[data[d]< self._minimum, d]=0.0      
        return dataN
    
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
    def __init__(self,mode='mean',window=25):
        self._mode = mode
        self._window = window
    
    def fit(self, X, y=None): 
        return self
    
    def transform(self, data): 
        statistics = pd.DataFrame()
        for c in data.columns: 
            if self._mode == 'mean': 
                statistics[c+' '+self._mode+" "+str(self._window)] = data[c].rolling(self._window).mean()
            elif self._mode == 'std': 
                statistics[c+' '+self._mode+" "+str(self._window)] = data[c].rolling(self._window).std()
            elif self._mode == 'range': 
                statistics[c+' '+self._mode+" "+str(self._window)] = data[c].rolling(self._window).max() - data[c].rolling(self._window).min()
            else: 
                raise Exception("mode: '"+self._mode+"' is not correct. Aviable modes are 'mean', 'std' and 'range'.")
        return statistics
                    
            
        