#!/bin/bash
## remove logs
## logs location is defined in log4j.xml
if [ -e '../../../logs/dealing.log' ]; then
 rm ../../../logs/statistics.log
else echo 'No any logs to remove.'
fi
