__author__ = 'Aleksandr Panov'
from tkinter import *


def msg():
    print('Hello stdout...')


top = Frame()
top.pack()
Label(top, text='Hello world').pack(side=TOP)
widget = Button(top, text='press', command=msg)
widget.pack(side=BOTTOM)
top.mainloop()
