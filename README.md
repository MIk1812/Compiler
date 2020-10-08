<center>
<img src="https://i.imgur.com/mIFqWgX.png" alt="img" width="150" />

</center>
<br><br>
<center><font size="6">Danmark Tekniske Universitet</font></center>

***

<center><font size="6">02332 Compilerteknik</font></center>
<center>9-10-2020</center>

<br>
<br>


| <img src="https://lh6.googleusercontent.com/JsVqJdYmObrAgDTAZygNwALQXUUSL496brNcD-rNZg59xJdhOaYL5QFkzPd9zI9yXjUSd0l_A70zZKHZs0AG-_-JYKHNAhv8ZpC3HkbevnnEE89GUuJmURqoz7WCDOiATZQBrgSo" alt="img" width="100"/><br> <b>Mikkel Danielsen, s183913</b> | <img src="https://lh5.googleusercontent.com/mGiheVfoHA6u79gCGW_s9nbAfGY-8EPSO1lYFwVwzH-jPyhjtPzLQYSlftJLGzz-miTUvX9jNeG4QPRYcGAst0OMPp8E7GfYNE8XdlbTxehAnJeFNftCaTZXyfzHCZBjawfOtwC0" alt="img" width="100"/><br><b>Volkan Isik, s180103</b> <tr></tr> |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| <img src="https://lh3.googleusercontent.com/cxw7Q3TX5pFLCv2p0Y5ZcUjLip1GlMQ2WrLItx8_RT5vUVbTgyxayjqfCLlKLERZVeOXDnjmqjuOsJ6VgCBX00ugI0eXKypyWMZrN-ZNM-4fdcNCo9FVeDL8hxJbLTuvfArri3Yq" alt="img" width="100" /><br><b> Mark Rune Mortensen, s174881</b> | <img src="https://lh5.googleusercontent.com/OxyrzNS3-o_n0bRhTncrZ5CrW3OZWia7_tW-fXe3kaClld1zzlzXPGV7HdIYYjqWCtS3jRwcVPBxbH-aKmszcZrOZbKz6al_z7gOyovUOaXlaXEaDyHZj56DTmcGNv1HLeSV6JGl" alt="img" width="100" /><br><b>Muhammad Talha Butt, s195475</b> |


<br><br>
[Github](https://github.com/MIk1812/Compiler)
---

### Task 1

Det regulære udtryk for *FLOAT* i det udleverede materiale forhindrede at man skrev *a=5-1*; man skulle skrive *a=5+(-1)*. Dette skyldtes, at *FLOAT* tillod +/- som præfix, hvilket resulterede i, at lexeren genkendte *5* og *-1* som selvstændige tokens uden nogen egentlig operator imellem. 

For at tillade *a=5-1*, ændrede vi *FLOAT* fra: `FLOAT : '-'? NUM+ ('.' NUM+)? ;` til `FLOAT :  NUM+ ('.' NUM+)? ;`
Dette bevirkede imidlertid, at man ikke kunne sætte en variable til *a=-5*.
For at tillade denne syntaks, tilføjede vi en ny production i vores context-free gramma, *expr*, kaldt `# Prefix`, der tillader et *+* eller *-* foran en FLOAT.

Rettet til:
```ANTLR
expr	: c=FLOAT     	      		# Constant
	| x=ID		      		# Variable
	| x=ID '[' e=expr ']'		# ArrayGet
	| s=ADDSUB e=FLOAT		# Prefix
	| e1=expr s=MULDEV e2=expr 	# MulDev
	| e1=expr s=ADDSUB e2=expr 	# AddSub	
	| '(' e=expr ')'      		# ParenthesisExpr
	;

MULDEV : ('*'|'/') ;
ADDSUB : ('+'|'-') ;
FLOAT : NUM+ ('.' NUM+)? ;
```
De regulære udtryk `MULDEV : ('*'|'/') ;` og `ADDSUB : ('+'|'-') ;` gør det muligt at vægte multiplikation/division og addition/substraktion lige i vores grammatik - altså sikre, at ligestillede operatorere korrekt associeres fra venstre mod højre. Desuden, fordi `MULDEV : ('*'|'/') ;` er angivet før `ADDSUB : ('+'|'-') ;` i ovenstående, vil den blive prioriteret højere. På denne måde sikre vi også, at vores operatorere får den rigtige precedence. Testcase 8 i bilagene demonstrerer netop dette.

Bemærk, eftersom ANTLR tillægger rækkefølgen af produktioner betydning, har vi generelt forsøgt at prioritere vores grammatik ift. "tiltrækning", der hvor det giver mening. 

### Task 2

I task 2 lavede vi en teoretisk løsning til implementeringen af conditional branching (if-then), for-loops og arrays. Dette blev gjort i en kopi af vores grammatik fil.

Teoretisk grammatik:
```ANTLR
command :  x=ID '[' e1=expr ']' '=' e2=expr ';'                      # ArraySet
        | 'if' '('c=condition')' 'then'? p=program 			# IfStatement
        | 'for' '(' x=expr '=' n1=expr '..' n2=expr ')' p=program 	# ForLoop
	;
    
expr : x=ID '[' e=expr ']'     # ArrayGet
```
For-loop'et blev efterfølgende ændret, så variablen der itereres over ikke er en expr, men derimod et ID, idet førstnævnte ikke gav mening. Variablen kunne da senere defineres i *Environment* i java via `env.setVariable(id, i + 0.0);`
```ANTLR
| 'for' '(' x=ID '=' n1=expr '..' n2=expr ')' p=program 	# ForLoop
```


### Task 3

Task 3  blev primært brugt til at implementere funktionaliteten fra task 2: *if then*, *for(i=0..n)* og *a[i]*. Vi lavede ekstra conditions i vores grammatik fil for at understøtte boolske udtryk bedre (*==, <=, >=, <, >, !, (), ||, &&*)


```ANTLR
condition 	: '!' c=condition			# Not
		| c1=condition '&&' c2=condition 	# And
		| c1=condition '||' c2=condition 	# Or
		| e1=expr '!=' e2=expr 		# Unequal
	  	| e1=expr '==' e2=expr 		# Equal
		| e1=expr '<' e2=expr 			# LessThan
		| e1=expr '>' e2=expr 	    	       # GreaterThan
		| e1=expr '<=' e2=expr                # LessThanOrEqual
		| e1=expr '>=' e2=expr 		# GreaterThanOrEqual
		| '(' c=condition ')'			# ParenthesisCondition
	  	;  
```
Til at teste den implementerede funktionalitet, udviklede vi en række små test, som alle er dokumenteret i bilagene. Vi benyttede også parse-tree en del; her ses det for testcase 9:

```java
x=20; y=5; z=7;
if(x<=z && y==5 && x>5 || !(y>7) && x>z)
output y;
```

![](https://i.imgur.com/KEwt8id.png)

Træet viser hvordan compileren forstår ovenstående kode. Interpreteren besøger hver knude og udfører den logik der er beskrevet i *main.java*. Bemærk hvordan "not" binder stærkere end "and" og "and" binder stærkere end "or", samt hvordan der associeres fra venstre mod højre, grundet den korrekte sortering af produktionerne i grammaen.  



### Task 4

Task 4 blevt udført parralelt med Task 3, alt funktionalitet i form af javakode bortset fra arrays var blevet implementeret i Task 3.
#### Java main implementation
```ANTLR
| x=ID '[' e1=expr ']' '=' e2=expr ';'	  # ArraySet
```
I dette eksemple vises, at vi har en regel/produktion, der hedder ArraySet bestående af (non)-terminale symboler hvortil der flere steder er knyttet labels, eks. "x=ID". ANTLR kombinerer disse elementer for at generere visitors med forskellige contexts (ctx). Ved hjælp af en context har vi adgang til alle labels inklusive tekst i en enkelt regel. 
```Java
public Double visitArraySet(implParser.ArraySetContext ctx){

    String id = ctx.x.getText();
    int index = visit(ctx.e1).intValue();
    Double equals = visit(ctx.e2);

    env.setVariable(id+"["+index+"]",equals);

    return null;
	}
```

Her vises array implementation for vores kode, vi bruger det 'trick', som beskrives i guiden. Nemlig at man giver hele expression eks. a[index] som et variable navn og dette gøres ved hjælp af Environment klasse der har en metode setVariable. 

Ovenfor vises at, vi kalder visit () på labels e1 og e2, hvis man kigger på det i forhold til parse træet, går metoden gennem træet, indtil den møder et "leaf", værdien returneres, afhængigt af hvor vi kalder visit() metoden.

Bemærk: På linje 4 caster vi værdien til integer, fordi den repræsentere index for et array og derfor ikke må skrives som et double.

linje 7-- kaldes setVariable() metode som så tager hele variable navn samt værdien som man gerne vil gemme.




## Bilag




### Testcase 1 

Vægtning af multiplikation og addition kontrolleres

```java
n=10;
result=0;
result=(n-5)+10*2;
output result;
``` 

| Forventet | Resultat |
| --------- | -------- |
| 25.0      | 25.0     |

### Testcase 2

If-statement kontrolleres i forbindelse med boolske udtryk

```java
n=4;
k=2;

if(n>k || n>0)
n=n-1;

if(n>k && n-1==k)
n=n-1;

output n;
```

|Forventet|Resultat|
|-----|-----|
|2.0|2.0|


    
### Testcase 3

For-loop kontrolleres

```java
a=2;
b=3;
result=6;
n=10;

for(i=0..n){
result=result-(a+b);
}
output result;
```

| Forventet | Resultat |
| --------- | -------- |
| -44.0     | -44.0    |





### Testcase 4

Arrays kontrolleres

```java
n=10;

for(i=0..n){
a[i]=i;
}
output a[7];
```

| Forventet | Resultat |
| --------- | -------- |
| 7.0       | 7.0      |

### Testcase 5

Arrays kontrolleres

```java
n=10;
k=3;
for(i=0..n){
    a[i]=i;
}
output a[k+2];
```

| Forventet | Resultat |
| --------- | -------- |
| 5.0       | 5.0      |



### Testcase 6

Not (!) kontrolleres

```java
result = 10;
if(!(4<3)) then result = 0;
output result;
```

| Forventet | Resultat |
| --------- | -------- |
| 0.0       | 0.0      |

### Testcase 7

Not (!) kontrolleres

```java
result = 10;
if(!(2<3)) then result = 0;
output result;
```

| Forventet | Resultat |
| --------- | -------- |
| 10.0      | 10.0     |


### Testcase 8

Tester precedence og associativitet i 
aritmetiske udtryk når flere operatorer blandes med og uden parentes. Pga. vores #Prefix løsning under "expr" i vores .g4-fil, er det desuden muligt at starte udtrykket med et minus.


```java
output -6+4*8/2-(3*5+1-2+3-4-2/17*5-1+(-4))*3;
```

| Forventet | Resultat |
| --------- | -------- |
| -12.235   | -12.235  |

Parse-træet kan med fordel også studeres her, hvor alt ser ud til at tjekke ud:
![](https://i.imgur.com/ZXNpn4V.png)



### Testcase 9

Tester precedence og associativitet i boolske udtryk. Det ses hvordan 'not' bindes stærkere end 'and' og 'and' bindes stærkere end 'or' samt hvordan der associeres fra venstre mod højre.

```java
x=20; y=5; z=7;
if(x<=z && y==5 && x>5 || !(y>7) && x>z)
output y;
```


| Forventet | Resultat |
| --------- | -------- |
| 5         | 5        |


### Testcase 10

Insertion-sort algoritmen her implementeret og testet. For at koden kan køre, er vi nødt til at definere a[-1], grundet vores lidt begrænsede grammatik. Dette gør, at conditon i while lykken ikke fejler, da vores parser ellers ikke kan finde variabel a[-1] i miljøet. Output kontrolleres for korrekte sortering. 

```java
n=5;

a[-1]=99999999999;
a[0]=12;
a[1]=11;
a[2]=13;
a[3]=5;
a[4]=6;


for(i=1..n){
key=a[i];
j=i;
while((j > 0) && (a[j-1] > key)){
a[j] = a[j-1];
j=j-1;
}
a[j]=key;
}
for(i=0..n){
output a[i];
}
```

| Forventet                          |    Resultat               |
| --------- | -------- |
| 5.0, 6.0, 11.0, 12.0, 13.0         | 5.0, 6.0, 11.0, 12.0, 13.0 |

### Testcase 11
Vi tester den udleverede impl_input.txt fil

```java
/* Example program for the imperative language impl */

n=5;
result=1;
while(n!=0){
  result=result*n;
  n=n+(-1);
}

output result;
````

|Forventet|Resultat|
|---------|--------|
|120.0    |120.0   |

### Testcase 12
Vi tester den udleverede impl_additional.txt fil


```java 
/* Example program for impl that tests  of the features required in
the assignment */

n=49;
k=6;
result=1;

while(n!=k && k!=0){
  result=result*n/k;
  n=n-1;
  k=k-1;
}   

output result;

n=100;

for(i=2..n){
  a[i]=1;
}

for(i=2..n){
  if(a[i]==1){
    output i;
    j=2*i;
    while(j<n){
      a[j]=0;
      j=j+i;
    }
  }
}
```

|Forventet|Resultat|
|---------|--------|
|1.3983816E7, 2.0, 3.0, 5.0, 7.0, 11.0, 13.0, 17.0, 19.0, 23.0, 29.0, 31.0, 37.0, 41.0, 43.0, 47.0, 53.0, 59.0, 61.0, 67.0, 71.0, 73.0, 79.0, 83.0, 89.0, 97.0|1.3983816E7, 2.0, 3.0, 5.0, 7.0, 11.0, 13.0, 17.0, 19.0, 23.0, 29.0, 31.0, 37.0, 41.0, 43.0, 47.0, 53.0, 59.0, 61.0, 67.0, 71.0, 73.0, 79.0, 83.0, 89.0, 97.0    | 






