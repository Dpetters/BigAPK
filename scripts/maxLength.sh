cat *.data.csv | awk '{split($0,a,","); print length(a)}' |awk 'function max(x){i=0;for(val in
x){if(i<=x[val]){i=x[val];}}return i;} function min(x){i=max(x);for(val in x){if(i>x[val]){i=x[val];}}return i;}
{a[$1]=$1;next} END{minimum=min(a);maximum=max(a);print maximum}'
