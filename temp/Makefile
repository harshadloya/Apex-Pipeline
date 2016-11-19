#Targets:
#  Default target: build project
#  clean:          remove all generated files.
#  submit:         build compress archive all project source files.

PROJECT = 	ApexSimulator

TARGET =	jar

SRC_FILES = \
  src \
  target \
  doc \
  build \
  build.xml \
  Makefile \
  README


CFLAGS = -g -Wall -std=c11

$(TARGET):  	
		ant

clean:		
		rm -rf build target $(PROJECT).tar.gz


submit:
		tar -cvzf $(PROJECT).tar.gz $(SRC_FILES)
