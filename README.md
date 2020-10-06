<center>
<img src="https://i.imgur.com/mIFqWgX.png" alt="img" width="200" />

</center>
<br><br>
<center><font size="6">Danmark Tekniske Universitet</font></center>

***

<center><font size="6">02332 Compilerteknik</font></center>


<br>
<br>


| <img src="https://lh6.googleusercontent.com/JsVqJdYmObrAgDTAZygNwALQXUUSL496brNcD-rNZg59xJdhOaYL5QFkzPd9zI9yXjUSd0l_A70zZKHZs0AG-_-JYKHNAhv8ZpC3HkbevnnEE89GUuJmURqoz7WCDOiATZQBrgSo" alt="img" width="100"/><br> <b>Mikkel Danielsen, s183913</b> | <img src="https://lh5.googleusercontent.com/mGiheVfoHA6u79gCGW_s9nbAfGY-8EPSO1lYFwVwzH-jPyhjtPzLQYSlftJLGzz-miTUvX9jNeG4QPRYcGAst0OMPp8E7GfYNE8XdlbTxehAnJeFNftCaTZXyfzHCZBjawfOtwC0" alt="img" width="100"/><br><b>Volkan Isik, s180103</b> <tr></tr> |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| <img src="https://lh3.googleusercontent.com/cxw7Q3TX5pFLCv2p0Y5ZcUjLip1GlMQ2WrLItx8_RT5vUVbTgyxayjqfCLlKLERZVeOXDnjmqjuOsJ6VgCBX00ugI0eXKypyWMZrN-ZNM-4fdcNCo9FVeDL8hxJbLTuvfArri3Yq" alt="img" width="100" /><br><b> Mark Rune Mortensen, s174881</b> | <img src="https://lh5.googleusercontent.com/OxyrzNS3-o_n0bRhTncrZ5CrW3OZWia7_tW-fXe3kaClld1zzlzXPGV7HdIYYjqWCtS3jRwcVPBxbH-aKmszcZrOZbKz6al_z7gOyovUOaXlaXEaDyHZj56DTmcGNv1HLeSV6JGl" alt="img" width="100" /><br><b>Muhammad Talha Butt, s195475</b> |


<br><br>

---

### Task 1

```
Test the calculator on several inputs:
does it always compute the desired result,
especially when we mix several operators without parentheses?
```

Den udleverede *FLOAT* i .g4 vil ikke tillade at man skriver *a=5-1*, man skal skrive *a=5+(-1)*. Dette skyldes, at minus oprindeligt blev tilladt som præfix på floats, hvorfor lexeren genkendte *5* og *-1* som selvstændige tokens uden nogen egentlig operator imellem. 

For at tillade *a=5-1* skal *FLOAT* ændres fra: `FLOAT : '-'? NUM+ ('.' NUM+)? ;` til `FLOAT :  NUM+ ('.' NUM+)? ;`
Men dette gør, at man ikke kan sætte en variable til *a=-5* uden at sige *a=0-5*.
For at tillade *a=-5* skal der tilføjes en ny *expr* kaldt `# Prefix`, den tjekker for om der står *+* eller *-* foran en FLOAT.

Rettet til:
```ANTLR

expr	: c=FLOAT     	      		# Constant
	| e1=expr s=MULDEV e2=expr 	# MulDev
	| e1=expr s=ADDSUB e2=expr 	# AddSub
	| s=ADDSUB e=FLOAT		# Prefix
	| x=ID		      		# Variable
	| x=ID '[' e=expr ']'		# ArrayGet
	| '(' e=expr ')'      		# ParenthesisExpr
	;


MULDEV : ('*'|'/') ;
ADDSUB : ('+'|'-') ;
FLOAT : NUM+ ('.' NUM+)? ;
```
`MULDEV : ('*'|'/') ;` og `ADDSUB : ('+'|'-') ;` gør at multiplikation,division og addition,substraktion bliver vægtet lige. Gramatiken behandes i main.java filen med if og else sætninger. Fordi `MULDEV : ('*'|'/') ;` kommer før `ADDSUB : ('+'|'-') ;` så vil den blive prioriteret højere.  På denne måde kan man håndtere de forskellige bindinger i vores grammatik.

### Task 2

![](https://i.imgur.com/xrhu10W.png)


For task 2 har vi lavet en teoretisk løsning til implementeringen af: *if then*, *for(i=0..2)* og *a[e]*
Dette er kun blevet gjort i en kopi af vores grammatik fil.

Teoretisk grammatik
```ANTLR
command : x=ID '=' e=expr ';'	         				# Assignment
	| x=ID '[' e1=expr ']' '=' e2=expr ';'			# ArraySet
	| 'output' e=expr ';'            				# Output
        | 'while' '('c=condition')' p=program 	 		# WhileLoop
	| 'if' '('c=condition')' 'then'? p=program 			# IfStatement
	| 'for' '(' x=expr '=' n1=expr '..' n2=expr ')' p=program 	# ForLoop
	;
```
#### Update
ForLoop er ændret så man kan genkende variablen som forloopet iterer. Variablen kan nu defineres i Environment: `env.setVariable(id, i + 0.0);`
```ANTLR
| 'for' '(' x=ID '=' n1=expr '..' n2=expr ')' p=program 	# ForLoop
```


### Task 3

Task 2 terorien er blevet brugt til at lave funktionaliteten for if then*, *for(i=0..2)* og *a[e]*
Der er rykket rundt i rækkefølgen under *expr* og multiplikation og division, addition og subtraktion er blevet lagt sammen (MulDiv, AddSub) så rækkefølgen af udregningerne er korrekte.
Vi har lavet ekstra conditions i vores grammatik fil for at understøtte: (*==, <=, >=, <, >, !,!(), ||, &&*)

Conditions
```ANTLR
condition      : e1=expr '!=' e2=expr 	        # Unequal
	  	| e1=expr '==' e2=expr 		# Equal
		| e1=expr '<' e2=expr 			# LessThan
		| e1=expr '>' e2=expr 			# GreaterThan
		| e1=expr '<=' e2=expr 		# LessThanOrEqual
		| e1=expr '>=' e2=expr 		# GreaterThanOrEqual
		| c1=condition '&&' c2=condition 	# And
		| c1=condition '||' c2=condition 	# Or
		| '!' c=condition			# Not
		| '(' c=condition ')'			# ParenthesisCondition
	  	; 
```

### Task 4

Task 4 blevt udført parralelt med Task 3, alt funktionalitet bortset fra arrays var blevet implementeret i Task 3.
#### Java main implementation
```ANTLR=
| x=ID '[' e1=expr ']' '=' e2=expr ';'	  # ArraySet
```

```Java=
public Double visitArraySet(implParser.ArraySetContext ctx){

    String id = ctx.x.getText();
    int index = visit(ctx.e1).intValue();
    Double equals = visit(ctx.e2);

    env.setVariable(id+"["+index+"]",equals);

    return null;
	}
```

Her vises array implementation for vores kode, vi bruger den 'trick' beskrives i guiden. Nemlig at man giver hele expression a[5] som et variable navn og dette gøres ved hjælp af Environment klasse der har en metode setVariable. 

linje 3--ved hjælp af ctx tager vi fat i x variable fra g4 file og dermed kalder getText() der returnere den string, x peger på.

linje 4-- For at tage fat i hviklet værdi har expr kalder vi visit() metode der returnere en double, som så bliver parset til int for index værdi af array ikke skal være en double.







## Bilag




### Testcase 1

Vægtning af multiplikation og addition kontrolleres.

```java=
n=10;
result=0;

result=(n-5)*2+10;

output result;
``` 

| Forventet | Resultat |
| --------- | -------- |
| 20.0      | 20.0     |

### Testcase 2

To variabler initaliseres med værdier 4 og 2. Derefter en if statement kontrolleres med to forskellige conditons med en or i midten.

```java=
n=4;
k=2;
if(n>k || n>0){
n=n-1;
}
if(n>k && n-1==k){
n=n-1;
}
output n;
```

|Forventet|Resultat|
|-----|-----|
|2.0|2.0|


    
### Testcase 3

Variabler initaliseres for a, b, result og n. Matematisk operation foretages i mellem variablerne over flere gange ved hjælp af for loop. Reultat læses når for loopet kørt færdig.

```java=
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

Array af længde n opereres hvor hver array elementer har værdien iterationsnummer. Derefter array element  a[7] kontrolleres.

```java=
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

To variable initaliseres for at angive længde af en array og en vilkårlig tal. Array a itereres igennem og initaliseres med iterations nummer. Output aflæses ved hjælp af angive variabel navn a[k+2].

```java=
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

Result bliver initaliseret som 10. Der bliver testet for om en not (!) expression bliver udført korrekt.

```java=
result = 10;
if(!(4<3)) then result = 0;
output result;
```

| Forventet | Resultat |
| --------- | -------- |
| 0.0       | 0.0      |

### Testcase 7

```java=
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


```java=
output -6+4*8/2-(3*5+1-2+3-4-2/17*5-1+(-4))*3;
```

| Forventet | Resultat |
| --------- | -------- |
| -12,235   | -12,235  |


### Testcase 9

Tester precedence og associativitet i boolske udtryk.
Tester Logical 'And' og 'Or' Her ses hvordan 'not' bindes stærkere end 'and' og 'and' bindes stærkere end 'or'. 

```java=
x=20;
y=5;
z=7;
if(x<=z && y==5 || !(y>7) && x>z){
output y;
}
```


| Forventet | Resultat |
| --------- | -------- |
| 5         | 5        |


### Testcase 10

En array længde initaliseres samt med en array af tilfældig defineret tal. For at koden kan køre er man nød til at implementere a[-1] . Dette gør at conditon i while lykken ikke fejler da vores parser kan ellers ikke finde variabel a[-1] i miljøet. Insertionsort algoritmen implementeres. Output kontrolleres for korrekte sortering. 

```java=
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
Vi tester den udleverede input.txt fil

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


```java= 
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






