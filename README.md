#GWTThreeJsEquirectangular
##better ones
these project much better.try first

https://github.com/spite/THREE.CubemapToEquirectangular

https://github.com/imgntn/j360

##what doing
trying three.js rendering convrt to equirectangular file.

I think there are a bug:''the total blobs' size cannot exceed about 500MiB''
https://bugs.chromium.org/p/chromium/issues/detail?id=375297

that why I think it's better to store indivisual file
##warnning
most of servlet save file to System.getProperty("user.home")+fileSeparator+"gwtthreejsequirectangular";
example Windows:c:\user\home\username\gwtthreejsequirectangular\

and clear all png images in directly when new-capturing start.

you should set init parameter in war/WEB-INF/web.xml