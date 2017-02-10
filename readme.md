Java List implementation over files

I needed a List<Long> 99,999,999,999 elements long, which wouldn't fit in ram of course, but could fit in 500gb as a 40-bit unsigned integer, these classes actually let you implement any List backed by a RandomAccessFile.

Enjoy!