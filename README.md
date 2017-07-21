# textteaser-java
Java version of automatic text summarizer [textteaser](https://github.com/DataTeaser/textteaser).

## Description
This is a java version of [textteaser](https://github.com/DataTeaser/textteaser), which was originally written in Scala and Python. This version works in 6 languages, which are : English, Spanish, French, Russian, Arabic and (simplified) Chinese. The parsers are mainly done by [StanfordCoreNLP](https://stanfordnlp.github.io/CoreNLP/), however, the parsing is done using default tokenizer (PTBTokenizer) for Russian.
To start using the package, you can either 1) Clone this repo and build yourself, or 2) Download the jar from this link (~ 2.29 GB). The file size is exteremly **high** due to 5 language models as it dependencies.


## Pre-Requisite
1. Maven 3 installed on local machine.
2. Java 8 installed on local machine.
3. Intellij IDEA (Optional).


## Build Instruction
1. Navigate to project root.
2. Compile the solution and its dependencies to single .jar :
```bash
mvn clean compile assembly:single
```


## Command Line Usage
Using command line, you can either summarize a text from a plain text file or by copying the text into the argument. If the text if provided using -t argument, the -f argument will be ignored. The minimal command to run the summarizer is :
```bash
java -cp textteaser-java.jar io.azrina.nlp.summarizer.Main -f <filename>
```
This command will generates summary from the text contained in a given filename. If you download the from the source, you can find the samples from src/resources directory. For example, you can run following command :
```bash
$ java -cp target/textteaser-java.jar io.azrina.nlp.summarizer.Main -f src/resources/sample-en.txt
```
However, we **highly** recommends to add title (-i) argument to improve the summary quality. For given samples, you can find the titles from src/resources/titles.txt. Here is the example on how to run the command using the title :
```bash
$ java -cp target/textteaser-java.jar io.azrina.nlp.summarizer.Main -f src/resources/sample-en.txt -i "Zika could end up costing Latin America and the Caribbean up to $18 billion, UN reports finds"
```
Using this, we will get much better summarizer result. 

> "In addition to the impact on public health, the tangible impact of the Zika outbreak, such as on gross domestic product (GDP), could cost the Latin American and the Caribbean region as much as $18 billion between 2015 and 2017, a new United Nations report has revealed. (...) The report Socio-economic impact assessment of Zika virus in Latin America and the Caribbean, prepared by the UN Development Programme (UNDP) in partnership with the International Federation of Red Cross and Red Crescent Societies (IFRC), has a particular focus on Brazil, Colombia and Suriname – countries that first reported the outbreak in October-November 2015. (...) “The Zika virus has highlighted, once again, the critical role that communities and local health workers play during health emergencies,” said IFRC Regional Director for the Americas Walter Cotte highlighting that community engagement strengthens local partnerships, resilience and reduces stigma. (...)"

In addition, don't forget to add language (-l) if you want to summarize text other than English. For example, 
```bash
$ java -cp target/textteaser-java.jar io.azrina.nlp.summarizer.Main -f src/resources/sample-ar.txt -i "تقرير أممي: تكلفة فيروس زيكا على بلدان الأميركتين تتراوح بين 7 و18 مليار دولار" -l ar
```
You can find full arguments below :

| arg        | alt           | description  |
| :-------------: |:-------------:| -----|
|  -t      | --text | Text content. |
| -f      | --file      | Input text file. Will be ignored if the text is provided using '-t' argument.|
| -i | --title      | Text title. Defaults to empty string. |
| -l | --lang | Text language. Supported languages : en, es, fr, ar, ru, zh-cn. Defaults to en. |
| -d | --delimiter| Delimiter between sentences. Defaults to ' (...)'.|
| -n | --num| Max number of sentences. Defaults to 3.|
| -h| --help| See help.|


## Usage in Java
```java
import io.azrina.nlp.summarizer.Summarizer

Summarizer summarizer = new Summarizer(lang); 
String result = summarizer.summarize(title, text, num, delimiter);
System.out.println(result);
```


## Usage in Scala
```scala
import io.azrina.nlp.summarizer.Summarizer

val summarizer = new Summarizer(lang)
val result : String = summarizer.summarize(title, text, num, delimiter)
println(result)
```

## Author
[Kania Azrina](https://github.com/kennyazrina), azrina@un.org 

## License
textteaser-java is available under MIT license. See LICENSE file for more information.
