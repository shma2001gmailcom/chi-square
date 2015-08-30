#!/bin/sh
## tar.gz project excluding classes and .svn
project_folder='statistics'

cd ../../
if [ ! -e ${project_folder} ]; then echo 'ERROR: no project folder ' ${project_folder} ' found'; exit 1; fi
tar -zcvf ${project_folder}.tar.gz ${project_folder} --exclude='*.class' --exclude-vcs --exclude='*target*' --exclude='*bin*'