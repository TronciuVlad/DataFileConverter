# DataFileConverter
Converter for Sketchbook Express data files

Upon repairing my old iPad's charging port I managed to find an old drawing app that I used to make doodles around 2015. The problem with this app was that it had its support dropped with ios 11 because it was 32bit. I managed to find some data saved in the iPad because I had the inspiration not to delete it even though it had no use anymore. Using iTunes and a really cool open source backup extractor (https://github.com/MaxiHuHe04/iTunes-Backup-Explorer) I managed to get the files back.

The problem with them is that they are not encoded in an usual way, but rather as these .data files. It would have been ideal if it was some sort of png or something else. I opened the image with a hex editor and I realised (knowing what that specific drawing is supposed to be) that the first bites (encoded as 00 00 00 ff) were hexagesimal rgba for the colour black. I worked on the assumption that there would be no compression, based on the sheer size of the binary file, and I was lucky that the image sizes were set in stone by the application itself (1920 x 2560).

The program is quite straight forward, it reads each 4 tuple of bytes and outputs it pixel by pixel as an array into a png file that is outputted into the file system. There is a problem with it: sometimes the images are 1 to 1 or 4/5 of the given resolution, sometimes it has nothing to do with it at all, so I am for now including the possibility for the programmer to play with the resolution until they get the proper image out.
