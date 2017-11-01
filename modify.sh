#!/bin/bash
cfile=` find -name '*.c' `
ccfile=` find -name '*.cc' `
jfile=` find -name '*.java' `
cppfile=` find -name '*.cpp' `
xfile=` find -name '*.xml' `
mfile=` find -name '*.mk' `
hfile=` find -name '*.h' `
sfile=` find -name '*.S' `
s1file=` find -name '*.s' `
modifyc(){
for fc in $cfile
do	
echo $fc
cfname=`basename -s .c $fc`
dcname=`dirname  $fc`
cat $fc > $dcname/$cfname.txt
rm -rf $fc
mv $dcname/$cfname.txt $dcname/$cfname.c
done
}
modifyj(){
for fj in $jfile
do	
echo $fj
jfname=`basename -s .java $fj` 
djname=`dirname  $fj`
cat $fj > $djname/$jfname.txt
rm -rf $fj
mv $djname/$jfname.txt $djname/$jfname.java
done 
}
modifycpp(){
for fcpp in $cppfile
do
echo $fcpp
cppfname=`basename -s .cpp $fcpp`
dcppname=`dirname  $fcpp`
cat $fcpp > $dcppname/$cppfname.txt
rm -rf $fcpp
mv $dcppname/$cppfname.txt $dcppname/$cppfname.cpp
done
}
modifyx(){
for fx in $xfile
do
echo $fx
xfname=`basename -s .xml $fx`
dxname=`dirname  $fx`
cat $fx > $dxname/$xfname.txt
rm -rf $fx
mv $dxname/$xfname.txt $dxname/$xfname.xml
done
}
modifym(){
for fm in $mfile
do
echo $fm
mfname=`basename -s .mk $fm`
dmname=`dirname  $fm`
cat $fm > $dmname/$mfname.txt
rm -rf $fm
mv $dmname/$mfname.txt $dmname/$mfname.mk
done
}

modifyh(){
for fh in $hfile
do
echo $fh
hfname=`basename -s .h $fh`
dhname=`dirname  $fh`
cat $fh > $dhname/$hfname.txt
rm -rf $fh
mv $dhname/$hfname.txt $dhname/$hfname.h
done
}
modifys(){
for fs in $sfile
do
echo $fs
sfname=`basename -s .S $fs`
dsname=`dirname  $fs`
cat $fs > $dsname/$sfname.txt
rm -rf $fs
mv $dsname/$sfname.txt $dsname/$sfname.S
done
}
modifys1(){
for fs1 in $s1file
do
echo $fs1
s1fname=`basename -s .s $fs1`
ds1name=`dirname  $fs1`
cat $fs1 > $ds1name/$s1fname.txt
rm -rf $fs1
mv $ds1name/$s1fname.txt $ds1name/$s1fname.S
done
}
modifycc(){
for fcc in $ccfile
do
echo $fcc
ccfname=`basename -s .cc $fcc`
dccname=`dirname  $fcc`
cat $fcc > $dccname/$ccfname.txt
rm -rf $fcc
mv $dccname/$ccfname.txt $dccname/$ccfname.cc
done
}
echo "1111111111111 $1"
if [ "$1" = "" ]; then
echo "0000000000000000000000"
modifyc
modifyj
modifycpp
modifyx
modifym
modifyh
modifys
modifys1
modifycc
elif [ "$1" = "c" ];then
echo "111111111111111111111"
modifyc
elif [ "$1" = "j" ];then
modifyj
elif [ "$1" = "cpp" ];then
modifycpp
elif [ "$1" = "x" ];then
modifyx
elif [ "$1" = "m" ];then
modifym
elif [ "$1" = "h" ];then
modifyh
elif [ "$1" = "s" ];then
modifys
elif [ "$1" = "s1" ];then
modifys1
elif [ "$1" = "cc" ];then
modifycc
fi

