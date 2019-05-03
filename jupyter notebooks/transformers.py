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
        self._fs = fs

    def fit(self, X, y=None):
        self._b, self._a = sg.butter(self._N,self._Wn,btype=self._btype,analog=self._analog,output=self._output, fs=self._fs)
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
    
    def __init__(self,mode='mean',window=25):
        """
        Calculates rolling statistics of the columns from data. 
        Parameters
        ----------
            mode: string, optional
                'mean': rolling mean
                'std': rolling standard deviation
                'max': rolling maximun value
                'min': rolling minimum value
                'range' : rolling ranges (difference between max and min)
            window: int, optional 
        """
        self._mode = mode
        self._window = window
        #Functions
        mean = lambda x: x.rolling(self._window).mean()
        std = lambda x: x.rolling(self._window).std()
        _max = lambda x: x.rolling(self._window).max()
        _min = lambda x: x.rolling(self._window).min()
        rang = lambda x: _max(x)-_min(x)
        self._functions = {'mean':mean,'std':std,'max':_max,'min':_min,'range':rang}
    
    def fit(self, X, y=None): 
        return self
    
    def transform(self, data): 
        statistics = pd.DataFrame()
        try:
            func = self._functions[self._mode]
        except:
            raise NameError('Unknown mode, use mean, std, max, min or range')
        for c in data.columns: 
            statistics[c+' '+self._mode+" "+str(self._window)] = func(data[c])
        return statistics

                  
class MoveTargetsTransformer(TransformerMixin):
    
    def __init__(self,window=25,mode='only'):
        """
        Transforms targets for rolling statics transformations
        
        Parameters
        ----------
            window: int, optional
                rolling static window
            mode: string, optional
                Mode of targets transformation.
                only = if all statics that compound it are true the final target will be true
                half = if at least statics that compound it are true the final target will be true
                start = if the first element of rolling are true the final target will be true
                end = if the last element of rolling are true the final target will be true
        """
        self._transformers = {'only':self._only_seizure,'half':self._half_seizure,'start':self._half_seizure,'end':self._end_seizure}
        self._window = window
        self._transform = self._transformers[mode]
        
    def fit(self, X, y=None):
        return self
    
    def transform(self, data):
        data = data.copy()
        
        return self._transform(data)
    
    def _only_seizure(self,data):
        """
        Target set to true if all data that compose it are true
        """
        trues = data.loc[data['target'] == True]
        
        i = trues.first_valid_index()
        
        data.loc[i:i+self._window-1, 'target'] = False 
        return data
    
    def _half_seizure(self,data):
        """
        Target set to true if at least the half that compose it are true
        """
        trues = data.loc[data['target'] == True]
        
        i = trues.first_valid_index()
        j = trues.last_valid_index()
        
        data.loc[i:i+int(self._window/2), 'target'] = False    
        data.loc[j:j+int(self._window/2), 'target'] = True
        
        return data
        
    def _start_seizure(self,data):
        """Target set to true if the first data that compose it are true"""
        data = self._only_seizure(data)
        trues = data.loc[data['target'] == True]
        i = trues.last_valid_index()
        
        data.loc[i:i+self._window-1,'target']=True
        return data
    
    def _end_seizure(self,data):
        """
        Target set to true if the last data that compose it are true
        """
        return data