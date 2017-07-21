package io.azrina.nlp.summarizer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.international.french.process.FrenchTokenizer;
import edu.stanford.nlp.international.spanish.process.SpanishTokenizer;
import edu.stanford.nlp.international.arabic.process.ArabicTokenizer;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.WhitespaceTokenizer;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

/**
 * <h1>Parser</h1> A class representing a parser whose methods are used to
 * process text documents and generate some features for text summarizer.
 *
 **/
public class Parser {

    String lang;

    // Local Variable Declaration
    private static final Double IDEAL = 20.0; // Ideal number for sentence length
    private static final String EN_STOPWORDS = "!!,?!,??,!?,`,``,'',-lrb-,-rrb-,-lsb-,-rsb-,.,:,;,\",',?,<,>,{,},[,],+,-,(,),&,%,$,@,!,^,#,*,..,...,'ll,'s,'m,a,about,above,after,again,against,all,am,an,and,any,are,aren't,as,at,be,because,been,before,being,below,between,both,but,by,can,can't,cannot,could,couldn't,did,didn't,do,does,doesn't,doing,don't,down,during,each,few,for,from,further,had,hadn't,has,hasn't,have,haven't,having,he,he'd,he'll,he's,her,here,here's,hers,herself,him,himself,his,how,how's,i,i'd,i'll,i'm,i've,if,in,into,is,isn't,it,it's,its,itself,let's,me,more,most,mustn't,my,myself,no,nor,not,of,off,on,once,only,or,other,ought,our,ours,ourselves,out,over,own,same,shan't,she,she'd,she'll,she's,should,shouldn't,so,some,such,than,that,that's,the,their,theirs,them,themselves,then,there,there's,these,they,they'd,they'll,they're,they've,this,those,through,to,too,under,until,up,very,was,wasn't,we,we'd,we'll,we're,we've,were,weren't,what,what's,when,when's,where,where's,which,while,who,who's,whom,why,why's,with,won't,would,wouldn't,you,you'd,you'll,you're,you've,your,yours,yourself,yourselves,###,return,arent,cant,couldnt,didnt,doesnt,dont,hadnt,hasnt,havent,hes,heres,hows,im,isnt,its,lets,mustnt,shant,shes,shouldnt,thats,theres,theyll,theyre,theyve,wasnt,were,werent,whats,whens,wheres,whos,whys,wont,wouldnt,youd,youll,youre,youve";
    private static final String FR_STOPWORDS = "lui, sera, dans, eussions, moi, quels, mon, le, fussiez, tu, eut, des, ils, serons, serez, vous, les, fussent, nous, avions, soyons, tes, il, qu, cet, ai, aurions, eûtes, étaient, as, avez, au, celà, que, sont, suis, je, ayons, aie, soient, seront, auraient, cette, notre, ceci, ait, leur, votre, sur, ces, se, ton, leurs, seras, cela, fussions, aies, étant, fusses, eues, elle, ses, ce, seraient, d, étiez, fûmes, auriez, eussiez, j, l, m, n, quelles, auras, te, ayez, s, t, même, eussent, avons, furent, toi, sois, soit, seriez, de, fus, fut, est, étée, serai, aviez, sans, soi, ici, du, son, avec, ma, étés, étions, me, un, aurai, une, été, par, pas, aura, quelle, en, sommes, sa, fûtes, es, et, eu, aient, eusses, eue, eût, ne, quel, ont, mais, fût, étais, était, eus, aux, qui, c, eux, serions, étées, aurais, aurait, ayant, nos, avaient, fusse, vos, mes, pour, eusse, on, à, eûmes, ou, ta, eurent, serais, serait, avait, avais, y, aurez, aurons, auront, êtes, soyez, la";
    private static final String AR_STOPWORDS = "فان, او, اى, اي, لها, كما, أنت, وإن, انت, حيث, خلال, تكون, وما, في, هناك, التى, التي, به, منها, عن, أ, أنه, ا, انه, تلك, وهى, وهي, قد, ومن, بان, بأن, ضمن, وكان, فيه, ف, بعض, وان, و, كان, حتى, الذين, هذا, نحو, ب, ألا, اذا, بعد, كل, لا, بين, إما, إلا, إلى, إلي, إنها, وفي, الا, أيضا, أنها, ثم, فيها, على, لك, الآن, لم, لن, فما, فأن, أو, أى, أي, جميع, هى, هي, انها, وكانت, مع, هذه, من, عند, عندما, إنه, لكن, عليه, وكل, ولا, ذلك, وهو, منذ, بها, الان, إن, منه, لدى, عليها, ولم, أن, وأن, ما, له, الذى, الذي, إذا, قبل, هو, فهو, فهى, فهي, أما, بينما, كانت, اما, غير, الى, الي, ولن, ايضا, يكون, ان";
    private static final String ES_STOPWORDS = "a,actualmente,acuerdo,adelante,ademas,además,adrede,afirmó,agregó,ahi,ahora,ahí,al,algo,alguna,algunas,alguno,algunos,algún,alli,allí,alrededor,ambos,ampleamos,antano,antaño,ante,anterior,antes,apenas,aproximadamente,aquel,aquella,aquellas,aquello,aquellos,aqui,aquél,aquélla,aquéllas,aquéllos,aquí,arriba,arribaabajo,aseguró,asi,así,atras,aun,aunque,ayer,añadió,aún,b,bajo,bastante,bien,breve,buen,buena,buenas,bueno,buenos,c,cada,casi,cerca,cierta,ciertas,cierto,ciertos,cinco,claro,comentó,como,con,conmigo,conocer,conseguimos,conseguir,considera,consideró,consigo,consigue,consiguen,consigues,contigo,contra,cosas,creo,cual,cuales,cualquier,cuando,cuanta,cuantas,cuanto,cuantos,cuatro,cuenta,cuál,cuáles,cuándo,cuánta,cuántas,cuánto,cuántos,cómo,d,da,dado,dan,dar,de,debajo,debe,deben,debido,decir,dejó,del,delante,demasiado,demás,dentro,deprisa,desde,despacio,despues,después,detras,detrás,dia,dias,dice,dicen,dicho,dieron,diferente,diferentes,dijeron,dijo,dio,donde,dos,durante,día,días,dónde,e,ejemplo,el,ella,ellas,ello,ellos,embargo,empleais,emplean,emplear,empleas,empleo,en,encima,encuentra,enfrente,enseguida,entonces,entre,era,eramos,eran,eras,eres,es,esa,esas,ese,eso,esos,esta,estaba,estaban,estado,estados,estais,estamos,estan,estar,estará,estas,este,esto,estos,estoy,estuvo,está,están,ex,excepto,existe,existen,explicó,expresó,f,fin,final,fue,fuera,fueron,fui,fuimos,g,general,gran,grandes,gueno,h,ha,haber,habia,habla,hablan,habrá,había,habían,hace,haceis,hacemos,hacen,hacer,hacerlo,haces,hacia,haciendo,hago,han,hasta,hay,haya,he,hecho,hemos,hicieron,hizo,horas,hoy,hubo,i,igual,incluso,indicó,informo,informó,intenta,intentais,intentamos,intentan,intentar,intentas,intento,ir,j,junto,k,l,la,lado,largo,las,le,lejos,les,llegó,lleva,llevar,lo,los,luego,lugar,m,mal,manera,manifestó,mas,mayor,me,mediante,medio,mejor,mencionó,menos,menudo,mi,mia,mias,mientras,mio,mios,mis,misma,mismas,mismo,mismos,modo,momento,mucha,muchas,mucho,muchos,muy,más,mí,mía,mías,mío,míos,n,nada,nadie,ni,ninguna,ningunas,ninguno,ningunos,ningún,no,nos,nosotras,nosotros,nuestra,nuestras,nuestro,nuestros,nueva,nuevas,nuevo,nuevos,nunca,o,ocho,os,otra,otras,otro,otros,p,pais,para,parece,parte,partir,pasada,pasado,paìs,peor,pero,pesar,poca,pocas,poco,pocos,podeis,podemos,poder,podria,podriais,podriamos,podrian,podrias,podrá,podrán,podría,podrían,poner,por,porque,posible,primer,primera,primero,primeros,principalmente,pronto,propia,propias,propio,propios,proximo,próximo,próximos,pudo,pueda,puede,pueden,puedo,pues,q,qeu,que,quedó,queremos,quien,quienes,quiere,quiza,quizas,quizá,quizás,quién,quiénes,qué,r,raras,realizado,realizar,realizó,repente,respecto,s,sabe,sabeis,sabemos,saben,saber,sabes,salvo,se,sea,sean,segun,segunda,segundo,según,seis,ser,sera,será,serán,sería,señaló,si,sido,siempre,siendo,siete,sigue,siguiente,sin,sino,sobre,sois,sola,solamente,solas,solo,solos,somos,son,soy,soyos,su,supuesto,sus,suya,suyas,suyo,sé,sí,sólo,t,tal,tambien,también,tampoco,tan,tanto,tarde,te,temprano,tendrá,tendrán,teneis,tenemos,tener,tenga,tengo,tenido,tenía,tercera,ti,tiempo,tiene,tienen,toda,todas,todavia,todavía,todo,todos,total,trabaja,trabajais,trabajamos,trabajan,trabajar,trabajas,trabajo,tras,trata,través,tres,tu,tus,tuvo,tuya,tuyas,tuyo,tuyos,tú,u,ultimo,un,una,unas,uno,unos,usa,usais,usamos,usan,usar,usas,uso,usted,ustedes,v,va,vais,valor,vamos,van,varias,varios,vaya,veces,ver,verdad,verdadera,verdadero,vez,vosotras,vosotros,voy,vuestra,vuestras,vuestro,vuestros,w,x,y,ya,yo,z,él,ésa,ésas,ése,ésos,ésta,éstas,éste,éstos,última,últimas,último,últimos";
    private static final String RU_STOPWORDS = "а,алло,без,белый,близко,более,больше,большой,будем,будет,будете,будешь,будто,буду,будут,будь,бы,бывает,бывь,был,была,были,было,быть,в,важная,важное,важные,важный,вам,вами,вас,ваш,ваша,ваше,ваши,вверх,вдали,вдруг,ведь,везде,вернуться,весь,вечер,взгляд,взять,вид,видеть,вместе,вниз,внизу,во,вода,война,вокруг,вон,вообще,вопрос,восемнадцатый,восемнадцать,восемь,восьмой,вот,впрочем,времени,время,все,всегда,всего,всем,всеми,всему,всех,всею,всю,всюду,вся,всё,второй,вы,выйти,г,где,главный,глаз,говорил,говорит,говорить,год,года,году,голова,голос,город,да,давать,давно,даже,далекий,далеко,дальше,даром,дать,два,двадцатый,двадцать,две,двенадцатый,двенадцать,дверь,двух,девятнадцатый,девятнадцать,девятый,девять,действительно,дел,делать,дело,день,деньги,десятый,десять,для,до,довольно,долго,должно,должный,дом,дорога,друг,другая,другие,других,друго,другое,другой,думать,душа,е,его,ее,ей,ему,если,есть,еще,ещё,ею,её,ж,ждать,же,жена,женщина,жизнь,жить,за,занят,занята,занято,заняты,затем,зато,зачем,здесь,земля,знать,значит,значить,и,идти,из,или,им,именно,иметь,ими,имя,иногда,их,к,каждая,каждое,каждые,каждый,кажется,казаться,как,какая,какой,кем,книга,когда,кого,ком,комната,кому,конец,конечно,которая,которого,которой,которые,который,которых,кроме,кругом,кто,куда,лежать,лет,ли,лицо,лишь,лучше,любить,люди,м,маленький,мало,мать,машина,между,меля,менее,меньше,меня,место,миллионов,мимо,минута,мир,мира,мне,много,многочисленная,многочисленное,многочисленные,многочисленный,мной,мною,мог,могут,мож,может,можно,можхо,мои,мой,мор,москва,мочь,моя,моё,мы,на,наверху,над,надо,назад,наиболее,найти,наконец,нам,нами,народ,нас,начала,начать,наш,наша,наше,наши,не,него,недавно,недалеко,нее,ней,некоторый,нельзя,нем,немного,нему,непрерывно,нередко,несколько,нет,нею,неё,ни,нибудь,ниже,низко,никакой,никогда,никто,никуда,ними,них,ничего,ничто,но,новый,нога,ночь,ну,нужно,нужный,нх,о,об,оба,обычно,один,одиннадцатый,одиннадцать,однажды,однако,одного,одной,оказаться,окно,около,он,она,они,оно,опять,особенно,остаться,от,ответить,отец,отовсюду,отсюда,очень,первый,перед,писать,плечо,по,под,подумать,пожалуйста,позже,пойти,пока,пол,получить,помнить,понимать,понять,пор,пора,после,последний,посмотреть,посреди,потом,потому,почему,почти,правда,прекрасно,при,про,просто,против,процентов,пятнадцатый,пятнадцать,пятый,пять,работа,работать,раз,разве,рано,раньше,ребенок,решить,россия,рука,русский,ряд,рядом,с,сам,сама,сами,самим,самими,самих,само,самого,самой,самом,самому,саму,самый,свет,свое,своего,своей,свои,своих,свой,свою,сделать,сеаой,себе,себя,сегодня,седьмой,сейчас,семнадцатый,семнадцать,семь,сидеть,сила,сих,сказал,сказала,сказать,сколько,слишком,слово,случай,смотреть,сначала,снова,со,собой,собою,советский,совсем,спасибо,спросить,сразу,стал,старый,стать,стол,сторона,стоять,страна,суть,считать,т,та,так,такая,также,таки,такие,такое,такой,там,твой,твоя,твоё,те,тебе,тебя,тем,теми,теперь,тех,то,тобой,тобою,товарищ,тогда,того,тоже,только,том,тому,тот,тою,третий,три,тринадцатый,тринадцать,ту,туда,тут,ты,тысяч,у,увидеть,уж,уже,улица,уметь,утро,хороший,хорошо,хотеть,хоть,хотя,хочешь,час,часто,часть,чаще,чего,человек,чем,чему,через,четвертый,четыре,четырнадцатый,четырнадцать,что,чтоб,чтобы,чуть,шестнадцатый,шестнадцать,шестой,шесть,эта,эти,этим,этими,этих,это,этого,этой,этом,этому,этот,эту,я";
    private static final String ZH_STOPWORDS = "、,。,〈,〉,《,》,一,一切,一则,一方面,一旦,一来,一样,一般,七,万一,三,上下,不仅,不但,不光,不单,不只,不如,不怕,不惟,不成,不拘,不比,不然,不特,不独,不管,不论,不过,不问,与,与其,与否,与此同时,且,两者,个,临,为,为了,为什么,为何,为着,乃,乃至,么,之,之一,之所以,之类,乌乎,乎,乘,九,也,也好,也罢,了,二,于,于是,于是乎,云云,五,人家,什么,什么样,从,从而,他,他人,他们,以,以便,以免,以及,以至,以至于,以致,们,任,任何,任凭,似的,但,但是,何,何况,何处,何时,作为,你,你们,使得,例如,依,依照,俺,俺们,倘,倘使,倘或,倘然,倘若,借,假使,假如,假若,像,八,六,兮,关于,其,其一,其中,其二,其他,其余,其它,其次,具体地说,具体说来,再者,再说,冒,冲,况且,几,几时,凭,凭借,则,别,别的,别说,到,前后,前者,加之,即,即令,即使,即便,即或,即若,又,及,及其,及至,反之,反过来,反过来说,另,另一方面,另外,只是,只有,只要,只限,叫,叮咚,可,可以,可是,可见,各,各个,各位,各种,各自,同,同时,向,向着,吓,吗,否则,吧,吧哒,吱,呀,呃,呕,呗,呜,呜呼,呢,呵,呸,呼哧,咋,和,咚,咦,咱,咱们,咳,哇,哈,哈哈,哉,哎,哎呀,哎哟,哗,哟,哦,哩,哪,哪个,哪些,哪儿,哪天,哪年,哪怕,哪样,哪边,哪里,哼,哼唷,唉,啊,啐,啥,啦,啪达,喂,喏,喔唷,嗡嗡,嗬,嗯,嗳,嘎,嘎登,嘘,嘛,嘻,嘿,四,因,因为,因此,因而,固然,在,在下,地,多,多少,她,她们,如,如上所述,如何,如其,如果,如此,如若,宁,宁可,宁愿,宁肯,它,它们,对,对于,将,尔后,尚且,就,就是,就是说,尽,尽管,岂但,己,并,并且,开外,开始,归,当,当着,彼,彼此,往,待,得,怎,怎么,怎么办,怎么样,怎样,总之,总的来看,总的来说,总的说来,总而言之,恰恰相反,您,慢说,我,我们,或,或是,或者,所,所以,打,把,抑或,拿,按,按照,换句话说,换言之,据,接着,故,故此,旁人,无宁,无论,既,既是,既然,时候,是,是的,替,有,有些,有关,有的,望,朝,朝着,本,本着,来,来着,极了,果然,果真,某,某个,某些,根据,正如,此,此外,此间,毋宁,每,每当,比,比如,比方,沿,沿着,漫说,焉,然则,然后,然而,照,照着,甚么,甚而,甚至,用,由,由于,由此可见,的,的话,相对而言,省得,着,着呢,矣,离,第,等,等等,管,紧接着,纵,纵令,纵使,纵然,经,经过,结果,给,继而,综上所述,罢了,者,而,而且,而况,而外,而已,而是,而言,能,腾,自,自个儿,自从,自各儿,自家,自己,自身,至,至于,若,若是,若非,莫若,虽,虽则,虽然,虽说,被,要,要不,要不是,要不然,要么,要是,让,论,设使,设若,该,诸位,谁,谁知,赶,起,起见,趁,趁着,越是,跟,较,较之,边,过,还是,还有,这,这个,这么,这么些,这么样,这么点儿,这些,这会儿,这儿,这就是说,这时,这样,这边,这里,进而,连,连同,通过,遵照,那,那个,那么,那么些,那么样,那些,那会儿,那儿,那时,那样,那边,那里,鄙人,鉴于,阿,除,除了,除此之外,除非,随,随着,零,非但,非徒,靠,顺,顺着,首先,︿,！,＃,＄,％,＆,（,）,＊,＋,，,０,１,２,３,４,５,６,７,８,９,：,；,＜,＞,？,＠,［,］,｛,｜,｝,～,￥";

    // Default constructor
    public Parser() {
        this.lang = "en";
    }

    // Constructor with param
    public Parser(String l){
        this.lang = l;
    }

    public List<CoreMap> getSentences(String document) {
        // Remove all newline and backslash
        document = document.replace("\\n", "").replace("\\r", "").replace("\\", "");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(getProperties(lang));
        Annotation annotation = pipeline.process(document);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        return sentences;
    }

    /**
     * This method is used to extract a terms and it frequencies (counts) from a
     * given list of sentences, excluding stopwords.
     *
     * @param List<CoreMap> sentences List of sentences
     * @return Counter<String> Terms and its frequencies
     *
     */
    public Counter<String> getTermFrequencies(List<CoreMap> sentences) {
        Counter<String> termFrequencies = new ClassicCounter<String>();
        // Perform count
        for (CoreMap sentence : sentences)
            for (CoreLabel cl : sentence.get(CoreAnnotations.TokensAnnotation.class))
                termFrequencies.incrementCount(
                        cl.get(CoreAnnotations.TextAnnotation.class).toLowerCase().replaceAll("\\p{P}", ""));

        // Remove stopwords
        for (String s : getStopWords(lang != null ? lang : "en")) {
            if (termFrequencies.containsKey(s)) {
                termFrequencies.remove(s);
            }
        }

        // Remove empty string
        if (termFrequencies.containsKey("")) {
            termFrequencies.remove("");
        }

        // System.out.println(termFrequencies.toString());
        return termFrequencies;
    }

    /**
     * This method is use to tokenize a sentence to list of string using
     * Stanford Tokenizer. The properties are taken from configuration specified
     * in edu.stanford.nlp.pipeline.TokenizerAnnotator
     *
     * @param String sentence Sentence to be tokenized
     * @param Boolean stopword If set to true, include stopword
     * @return List<String> List of unique keywords (token) from a given sentence
     * @throws IOException
     *
     */
    public List<String> tokenize(String sentence, Boolean stopword) {

        TokenizerFactory<CoreLabel> factory = null;

        List<String> tokenized_sentence = new ArrayList<String>();

        // Remove punctuation
        sentence = sentence.replaceAll("\\p{P}", "");

        // Initialize tokenizer based on the language
        switch (lang) {
            case "en":
                factory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "invertible,ptb3Escaping=true");
                tokenized_sentence = getTokenizedSentence(sentence, stopword, getStopWords(lang), factory);
                break;
            case "es":
                factory = SpanishTokenizer.factory(new CoreLabelTokenFactory(), "invertible,ptb3Escaping=true,splitAll=true");
                tokenized_sentence = getTokenizedSentence(sentence, stopword, getStopWords(lang), factory);
                break;
            case "fr":
                factory = FrenchTokenizer.factory(new CoreLabelTokenFactory(), "");
                tokenized_sentence = getTokenizedSentence(sentence, stopword, getStopWords(lang), factory);
                break;
            case "ru":
                factory = WhitespaceTokenizer.newCoreLabelTokenizerFactory();
                tokenized_sentence = getTokenizedSentence(sentence, stopword, getStopWords(lang), factory);
                break;
            case "ar":
                factory = ArabicTokenizer.factory();
                tokenized_sentence = getTokenizedSentence(sentence, stopword, getStopWords(lang), factory);
                break;
            case "zh-cn":
                factory = WhitespaceTokenizer.newCoreLabelTokenizerFactory();
                tokenized_sentence = getTokenizedSentence(sentence, stopword, getStopWords(lang), factory);
                break;
            default:
                // Defaults to english
                factory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "invertible, ptb3Escaping=true");
                tokenized_sentence = getTokenizedSentence(sentence,stopword, getStopWords(lang), factory);
        }

        return tokenized_sentence;
    }


    public List<String> getTokenizedSentence(String sentence, Boolean stopword, List<String> stopWords, TokenizerFactory<CoreLabel> factory) {
        List<String> tokenized_sentence = new ArrayList<String>();
        if (null != factory) {

            // Perform tokenizing
            List<CoreLabel> tokens = factory.getTokenizer(new StringReader(sentence)).tokenize();

            for (CoreLabel token : tokens) {
                String keyword = token.originalText().toLowerCase();
                // Include stopword
                if (stopword) {
                    tokenized_sentence.add(keyword);
                } else {
                    // Only keep unique keywords and non-stopword word
                    if (stopWords.indexOf(keyword) < 0) {
                        tokenized_sentence.add(keyword);
                    }
                }
            }
        }
        return tokenized_sentence;
    }

    /**
     * This method is used to calculate a title feature score from a given
     * sentence by intersecting their keywords.
     *
     * @param List<String> title_words List of unique keywords in title
     * @param List<String> sentence_words List of unique keywords in sentence
     * @return Double Sentence title feature (score)
     */
    public Double getTitleFeature(List<String> title_words, List<String> sentence_words) {

        List<String> intersection = new ArrayList<String>(sentence_words);
        intersection.retainAll(title_words);

        Double k = intersection.size() / (title_words.size() + 1.0);

        return k;
    }

    /**
     * This method is used to calculate sentence length feature score from a
     * given sentence by comparing its length to ideal sentence length.
     *
     * @param List<String> sentence_words List of unique keywords in sentence.
     * @return Double Sentence length feature (score) @// TODO: 12/8/16 Evaluate if using unique tokenied sentence is
     * better than keeping duplicated words
     */
    public Double getLengthFeature(List<String> sentence_words) {
        Double k = (IDEAL - Math.abs(IDEAL - sentence_words.size())) / IDEAL;
        return k;
    }

    /**
     * This method is used to calculate sentence position feature score from a
     * given sentence by comparing its position to overall paragraph.
     *
     * @param int sentence_index Sentence position in paragraph
     * @param int sentence_length Number of sentences in paragraph
     * @return Double Sentence position feature (score)
     */
    public Double getPositionFeature(int sentence_index, int sentence_length) {

        Double normalized = (sentence_index + 1) / (sentence_length * 1.0);

        if (normalized > 0 && normalized <= 0.1)
            return 0.17;
        else if (normalized > 0.1 && normalized <= 0.2)
            return 0.23;
        else if (normalized > 0.2 && normalized <= 0.3)
            return 0.14;
        else if (normalized > 0.3 && normalized <= 0.4)
            return 0.08;
        else if (normalized > 0.4 && normalized <= 0.5)
            return 0.05;
        else if (normalized > 0.5 && normalized <= 0.6)
            return 0.04;
        else if (normalized > 0.6 && normalized <= 0.7)
            return 0.06;
        else if (normalized > 0.7 && normalized <= 0.8)
            return 0.04;
        else if (normalized > 0.8 && normalized <= 0.9)
            return 0.04;
        else if (normalized > 0.9 && normalized <= 1.0)
            return 0.15;
        else
            return 0.0;
    }

    /**
     * This method is used to get stopwords from specified language. (https://github.com/6/stopwords-json)
     *
     * @param String lang Language code of the text (ISO 639-2 Code)
     * @return List<String> List of Stopwords
     */
    private List<String> getStopWords(String lang) {
        List<String> stopWords = null;

        switch (lang) {
            case "en":
                stopWords = new ArrayList<String>(Arrays.asList(EN_STOPWORDS.split(",")));
                break;
            case "es":
                stopWords = new ArrayList<String>(Arrays.asList(ES_STOPWORDS.split(",")));
                break;
            case "fr":
                stopWords = new ArrayList<String>(Arrays.asList(FR_STOPWORDS.split(",")));
                break;
            case "ar":
                stopWords = new ArrayList<String>(Arrays.asList(AR_STOPWORDS.split(",")));
                break;
            case "zh-cn":
                stopWords = new ArrayList<String>(Arrays.asList(ZH_STOPWORDS.split(",")));
                break;
            case "ru":
                stopWords = new ArrayList<String>(Arrays.asList(RU_STOPWORDS.split(",")));
                break;
            default:
                stopWords = new ArrayList<String>(Arrays.asList(EN_STOPWORDS.split(",")));
        }

        return stopWords;
    }

    /**
     * This method is used to set Stanford Core NLP Annotator Pipeline
     * properties based on language specified.
     * @link http://stanfordnlp.github.io/CoreNLP/human-languages.html
     * @param String lang Language code of the text (ISO 639-2 Code)
     * @return Properties Stanford Core NLP Annotator Pipeline Properties
     */

    public Properties getProperties(String lang) {
        Properties props = new Properties();

        switch (lang) {
            case "en":
                // use default properties
                break;
            case "es":
                props = StringUtils.argsToProperties("-props", "StanfordCoreNLP-spanish.properties");
                break;
            case "fr":
                props = StringUtils.argsToProperties("-props", "StanfordCoreNLP-french.properties");
                break;
            case "zh-cn":
                props = StringUtils.argsToProperties("-props", "StanfordCoreNLP-chinese.properties");
                break;
            case "ar":
                props = StringUtils.argsToProperties("-props", "StanfordCoreNLP-arabic.properties");
                break;
            case "ru":
                // use defaults; defaults to english
                break;

        }
        props.setProperty("annotators", "tokenize, ssplit");  // override annotators
        return props;
    }

}

