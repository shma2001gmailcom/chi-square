#!/bin/sh
## open logs in text editor
if [ -e '../../../logs/statistics.log' ]; then
 gedit ../../../logs/statistics.log
else echo "No any logs to view."
fi