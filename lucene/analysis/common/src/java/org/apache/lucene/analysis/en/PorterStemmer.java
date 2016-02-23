begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.en
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|en
package|;
end_package

begin_comment
comment|/*     Porter stemmer in Java. The original paper is in         Porter, 1980, An algorithm for suffix stripping, Program, Vol. 14,        no. 3, pp 130-137,     See also http://www.tartarus.org/~martin/PorterStemmer/index.html     Bug 1 (reported by Gonzalo Parra 16/10/99) fixed as marked below.    Tthe words 'aed', 'eed', 'oed' leave k at 'a' for step 3, and b[k-1]    is then out outside the bounds of b.     Similarly,     Bug 2 (reported by Steve Dyrdahl 22/2/00) fixed as marked below.    'ion' by itself leaves j = -1 in the test for 'ion' in step 5, and    b[j] is then outside the bounds of b.     Release 3.     [ This version is derived from Release 3, modified by Brian Goetz to      optimize for fewer object creations.  ]  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ArrayUtil
import|;
end_import

begin_comment
comment|/**  *  * Stemmer, implementing the Porter Stemming Algorithm  *  * The Stemmer class transforms a word into its root form.  The input  * word can be provided a character at time (by calling add()), or at once  * by calling one of the various stem(something) methods.  */
end_comment

begin_class
DECL|class|PorterStemmer
class|class
name|PorterStemmer
block|{
DECL|field|b
specifier|private
name|char
index|[]
name|b
decl_stmt|;
DECL|field|i
specifier|private
name|int
name|i
decl_stmt|,
comment|/* offset into b */
DECL|field|j
DECL|field|k
DECL|field|k0
name|j
decl_stmt|,
name|k
decl_stmt|,
name|k0
decl_stmt|;
DECL|field|dirty
specifier|private
name|boolean
name|dirty
init|=
literal|false
decl_stmt|;
DECL|field|INITIAL_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_SIZE
init|=
literal|50
decl_stmt|;
DECL|method|PorterStemmer
specifier|public
name|PorterStemmer
parameter_list|()
block|{
name|b
operator|=
operator|new
name|char
index|[
name|INITIAL_SIZE
index|]
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * reset() resets the stemmer so it can stem another word.  If you invoke    * the stemmer by calling add(char) and then stem(), you must call reset()    * before starting another word.    */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|i
operator|=
literal|0
expr_stmt|;
name|dirty
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Add a character to the word being stemmed.  When you are finished    * adding characters, you can call stem(void) to process the word.    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
if|if
condition|(
name|b
operator|.
name|length
operator|<=
name|i
condition|)
block|{
name|b
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|b
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|b
index|[
name|i
operator|++
index|]
operator|=
name|ch
expr_stmt|;
block|}
comment|/**    * After a word has been stemmed, it can be retrieved by toString(),    * or a reference to the internal buffer can be retrieved by getResultBuffer    * and getResultLength (which is generally more efficient.)    */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
return|;
block|}
comment|/**    * Returns the length of the word resulting from the stemming process.    */
DECL|method|getResultLength
specifier|public
name|int
name|getResultLength
parameter_list|()
block|{
return|return
name|i
return|;
block|}
comment|/**    * Returns a reference to a character buffer containing the results of    * the stemming process.  You also need to consult getResultLength()    * to determine the length of the result.    */
DECL|method|getResultBuffer
specifier|public
name|char
index|[]
name|getResultBuffer
parameter_list|()
block|{
return|return
name|b
return|;
block|}
comment|/* cons(i) is true<=> b[i] is a consonant. */
DECL|method|cons
specifier|private
specifier|final
name|boolean
name|cons
parameter_list|(
name|int
name|i
parameter_list|)
block|{
switch|switch
condition|(
name|b
index|[
name|i
index|]
condition|)
block|{
case|case
literal|'a'
case|:
case|case
literal|'e'
case|:
case|case
literal|'i'
case|:
case|case
literal|'o'
case|:
case|case
literal|'u'
case|:
return|return
literal|false
return|;
case|case
literal|'y'
case|:
return|return
operator|(
name|i
operator|==
name|k0
operator|)
condition|?
literal|true
else|:
operator|!
name|cons
argument_list|(
name|i
operator|-
literal|1
argument_list|)
return|;
default|default:
return|return
literal|true
return|;
block|}
block|}
comment|/* m() measures the number of consonant sequences between k0 and j. if c is      a consonant sequence and v a vowel sequence, and<..> indicates arbitrary      presence,<c><v>       gives 0<c>vc<v>     gives 1<c>vcvc<v>   gives 2<c>vcvcvc<v> gives 3           ....   */
DECL|method|m
specifier|private
specifier|final
name|int
name|m
parameter_list|()
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
name|k0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|i
operator|>
name|j
condition|)
return|return
name|n
return|;
if|if
condition|(
operator|!
name|cons
argument_list|(
name|i
argument_list|)
condition|)
break|break;
name|i
operator|++
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|i
operator|>
name|j
condition|)
return|return
name|n
return|;
if|if
condition|(
name|cons
argument_list|(
name|i
argument_list|)
condition|)
break|break;
name|i
operator|++
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
name|n
operator|++
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|i
operator|>
name|j
condition|)
return|return
name|n
return|;
if|if
condition|(
operator|!
name|cons
argument_list|(
name|i
argument_list|)
condition|)
break|break;
name|i
operator|++
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
block|}
block|}
comment|/* vowelinstem() is true<=> k0,...j contains a vowel */
DECL|method|vowelinstem
specifier|private
specifier|final
name|boolean
name|vowelinstem
parameter_list|()
block|{
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
name|k0
init|;
name|i
operator|<=
name|j
condition|;
name|i
operator|++
control|)
if|if
condition|(
operator|!
name|cons
argument_list|(
name|i
argument_list|)
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
comment|/* doublec(j) is true<=> j,(j-1) contain a double consonant. */
DECL|method|doublec
specifier|private
specifier|final
name|boolean
name|doublec
parameter_list|(
name|int
name|j
parameter_list|)
block|{
if|if
condition|(
name|j
operator|<
name|k0
operator|+
literal|1
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|b
index|[
name|j
index|]
operator|!=
name|b
index|[
name|j
operator|-
literal|1
index|]
condition|)
return|return
literal|false
return|;
return|return
name|cons
argument_list|(
name|j
argument_list|)
return|;
block|}
comment|/* cvc(i) is true<=> i-2,i-1,i has the form consonant - vowel - consonant      and also if the second c is not w,x or y. this is used when trying to      restore an e at the end of a short word. e.g.            cav(e), lov(e), hop(e), crim(e), but           snow, box, tray.    */
DECL|method|cvc
specifier|private
specifier|final
name|boolean
name|cvc
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|i
operator|<
name|k0
operator|+
literal|2
operator|||
operator|!
name|cons
argument_list|(
name|i
argument_list|)
operator|||
name|cons
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|||
operator|!
name|cons
argument_list|(
name|i
operator|-
literal|2
argument_list|)
condition|)
return|return
literal|false
return|;
else|else
block|{
name|int
name|ch
init|=
name|b
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'w'
operator|||
name|ch
operator|==
literal|'x'
operator|||
name|ch
operator|==
literal|'y'
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|ends
specifier|private
specifier|final
name|boolean
name|ends
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|int
name|l
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|o
init|=
name|k
operator|-
name|l
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|o
operator|<
name|k0
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|l
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|b
index|[
name|o
operator|+
name|i
index|]
operator|!=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
return|return
literal|false
return|;
name|j
operator|=
name|k
operator|-
name|l
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/* setto(s) sets (j+1),...k to the characters in the string s, readjusting      k. */
DECL|method|setto
name|void
name|setto
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|int
name|l
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|o
init|=
name|j
operator|+
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|l
condition|;
name|i
operator|++
control|)
name|b
index|[
name|o
operator|+
name|i
index|]
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|k
operator|=
name|j
operator|+
name|l
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
comment|/* r(s) is used further down. */
DECL|method|r
name|void
name|r
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|m
argument_list|()
operator|>
literal|0
condition|)
name|setto
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|/* step1() gets rid of plurals and -ed or -ing. e.g.             caresses  ->  caress            ponies    ->  poni            ties      ->  ti            caress    ->  caress            cats      ->  cat             feed      ->  feed            agreed    ->  agree            disabled  ->  disable             matting   ->  mat            mating    ->  mate            meeting   ->  meet            milling   ->  mill            messing   ->  mess             meetings  ->  meet    */
DECL|method|step1
specifier|private
specifier|final
name|void
name|step1
parameter_list|()
block|{
if|if
condition|(
name|b
index|[
name|k
index|]
operator|==
literal|'s'
condition|)
block|{
if|if
condition|(
name|ends
argument_list|(
literal|"sses"
argument_list|)
condition|)
name|k
operator|-=
literal|2
expr_stmt|;
elseif|else
if|if
condition|(
name|ends
argument_list|(
literal|"ies"
argument_list|)
condition|)
name|setto
argument_list|(
literal|"i"
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|b
index|[
name|k
operator|-
literal|1
index|]
operator|!=
literal|'s'
condition|)
name|k
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"eed"
argument_list|)
condition|)
block|{
if|if
condition|(
name|m
argument_list|()
operator|>
literal|0
condition|)
name|k
operator|--
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|ends
argument_list|(
literal|"ed"
argument_list|)
operator|||
name|ends
argument_list|(
literal|"ing"
argument_list|)
operator|)
operator|&&
name|vowelinstem
argument_list|()
condition|)
block|{
name|k
operator|=
name|j
expr_stmt|;
if|if
condition|(
name|ends
argument_list|(
literal|"at"
argument_list|)
condition|)
name|setto
argument_list|(
literal|"ate"
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|ends
argument_list|(
literal|"bl"
argument_list|)
condition|)
name|setto
argument_list|(
literal|"ble"
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|ends
argument_list|(
literal|"iz"
argument_list|)
condition|)
name|setto
argument_list|(
literal|"ize"
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|doublec
argument_list|(
name|k
argument_list|)
condition|)
block|{
name|int
name|ch
init|=
name|b
index|[
name|k
operator|--
index|]
decl_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'l'
operator|||
name|ch
operator|==
literal|'s'
operator|||
name|ch
operator|==
literal|'z'
condition|)
name|k
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|m
argument_list|()
operator|==
literal|1
operator|&&
name|cvc
argument_list|(
name|k
argument_list|)
condition|)
name|setto
argument_list|(
literal|"e"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* step2() turns terminal y to i when there is another vowel in the stem. */
DECL|method|step2
specifier|private
specifier|final
name|void
name|step2
parameter_list|()
block|{
if|if
condition|(
name|ends
argument_list|(
literal|"y"
argument_list|)
operator|&&
name|vowelinstem
argument_list|()
condition|)
block|{
name|b
index|[
name|k
index|]
operator|=
literal|'i'
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/* step3() maps double suffices to single ones. so -ization ( = -ize plus      -ation) maps to -ize etc. note that the string before the suffix must give      m()> 0. */
DECL|method|step3
specifier|private
specifier|final
name|void
name|step3
parameter_list|()
block|{
if|if
condition|(
name|k
operator|==
name|k0
condition|)
return|return;
comment|/* For Bug 1 */
switch|switch
condition|(
name|b
index|[
name|k
operator|-
literal|1
index|]
condition|)
block|{
case|case
literal|'a'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"ational"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ate"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"tional"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"tion"
argument_list|)
expr_stmt|;
break|break;
block|}
break|break;
case|case
literal|'c'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"enci"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ence"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"anci"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ance"
argument_list|)
expr_stmt|;
break|break;
block|}
break|break;
case|case
literal|'e'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"izer"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ize"
argument_list|)
expr_stmt|;
break|break;
block|}
break|break;
case|case
literal|'l'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"bli"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ble"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"alli"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"al"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"entli"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ent"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"eli"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"e"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"ousli"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ous"
argument_list|)
expr_stmt|;
break|break;
block|}
break|break;
case|case
literal|'o'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"ization"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ize"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"ation"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ate"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"ator"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ate"
argument_list|)
expr_stmt|;
break|break;
block|}
break|break;
case|case
literal|'s'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"alism"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"al"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"iveness"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ive"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"fulness"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ful"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"ousness"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ous"
argument_list|)
expr_stmt|;
break|break;
block|}
break|break;
case|case
literal|'t'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"aliti"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"al"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"iviti"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ive"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"biliti"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ble"
argument_list|)
expr_stmt|;
break|break;
block|}
break|break;
case|case
literal|'g'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"logi"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"log"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/* step4() deals with -ic-, -full, -ness etc. similar strategy to step3. */
DECL|method|step4
specifier|private
specifier|final
name|void
name|step4
parameter_list|()
block|{
switch|switch
condition|(
name|b
index|[
name|k
index|]
condition|)
block|{
case|case
literal|'e'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"icate"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ic"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"ative"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|""
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"alize"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"al"
argument_list|)
expr_stmt|;
break|break;
block|}
break|break;
case|case
literal|'i'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"iciti"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ic"
argument_list|)
expr_stmt|;
break|break;
block|}
break|break;
case|case
literal|'l'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"ical"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|"ic"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ends
argument_list|(
literal|"ful"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|""
argument_list|)
expr_stmt|;
break|break;
block|}
break|break;
case|case
literal|'s'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"ness"
argument_list|)
condition|)
block|{
name|r
argument_list|(
literal|""
argument_list|)
expr_stmt|;
break|break;
block|}
break|break;
block|}
block|}
comment|/* step5() takes off -ant, -ence etc., in context<c>vcvc<v>. */
DECL|method|step5
specifier|private
specifier|final
name|void
name|step5
parameter_list|()
block|{
if|if
condition|(
name|k
operator|==
name|k0
condition|)
return|return;
comment|/* for Bug 1 */
switch|switch
condition|(
name|b
index|[
name|k
operator|-
literal|1
index|]
condition|)
block|{
case|case
literal|'a'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"al"
argument_list|)
condition|)
break|break;
return|return;
case|case
literal|'c'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"ance"
argument_list|)
condition|)
break|break;
if|if
condition|(
name|ends
argument_list|(
literal|"ence"
argument_list|)
condition|)
break|break;
return|return;
case|case
literal|'e'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"er"
argument_list|)
condition|)
break|break;
return|return;
case|case
literal|'i'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"ic"
argument_list|)
condition|)
break|break;
return|return;
case|case
literal|'l'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"able"
argument_list|)
condition|)
break|break;
if|if
condition|(
name|ends
argument_list|(
literal|"ible"
argument_list|)
condition|)
break|break;
return|return;
case|case
literal|'n'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"ant"
argument_list|)
condition|)
break|break;
if|if
condition|(
name|ends
argument_list|(
literal|"ement"
argument_list|)
condition|)
break|break;
if|if
condition|(
name|ends
argument_list|(
literal|"ment"
argument_list|)
condition|)
break|break;
comment|/* element etc. not stripped before the m */
if|if
condition|(
name|ends
argument_list|(
literal|"ent"
argument_list|)
condition|)
break|break;
return|return;
case|case
literal|'o'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"ion"
argument_list|)
operator|&&
name|j
operator|>=
literal|0
operator|&&
operator|(
name|b
index|[
name|j
index|]
operator|==
literal|'s'
operator|||
name|b
index|[
name|j
index|]
operator|==
literal|'t'
operator|)
condition|)
break|break;
comment|/* j>= 0 fixes Bug 2 */
if|if
condition|(
name|ends
argument_list|(
literal|"ou"
argument_list|)
condition|)
break|break;
return|return;
comment|/* takes care of -ous */
case|case
literal|'s'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"ism"
argument_list|)
condition|)
break|break;
return|return;
case|case
literal|'t'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"ate"
argument_list|)
condition|)
break|break;
if|if
condition|(
name|ends
argument_list|(
literal|"iti"
argument_list|)
condition|)
break|break;
return|return;
case|case
literal|'u'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"ous"
argument_list|)
condition|)
break|break;
return|return;
case|case
literal|'v'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"ive"
argument_list|)
condition|)
break|break;
return|return;
case|case
literal|'z'
case|:
if|if
condition|(
name|ends
argument_list|(
literal|"ize"
argument_list|)
condition|)
break|break;
return|return;
default|default:
return|return;
block|}
if|if
condition|(
name|m
argument_list|()
operator|>
literal|1
condition|)
name|k
operator|=
name|j
expr_stmt|;
block|}
comment|/* step6() removes a final -e if m()> 1. */
DECL|method|step6
specifier|private
specifier|final
name|void
name|step6
parameter_list|()
block|{
name|j
operator|=
name|k
expr_stmt|;
if|if
condition|(
name|b
index|[
name|k
index|]
operator|==
literal|'e'
condition|)
block|{
name|int
name|a
init|=
name|m
argument_list|()
decl_stmt|;
if|if
condition|(
name|a
operator|>
literal|1
operator|||
name|a
operator|==
literal|1
operator|&&
operator|!
name|cvc
argument_list|(
name|k
operator|-
literal|1
argument_list|)
condition|)
name|k
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|b
index|[
name|k
index|]
operator|==
literal|'l'
operator|&&
name|doublec
argument_list|(
name|k
argument_list|)
operator|&&
name|m
argument_list|()
operator|>
literal|1
condition|)
name|k
operator|--
expr_stmt|;
block|}
comment|/**    * Stem a word provided as a String.  Returns the result as a String.    */
DECL|method|stem
specifier|public
name|String
name|stem
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|stem
argument_list|(
name|s
operator|.
name|toCharArray
argument_list|()
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
condition|)
return|return
name|toString
argument_list|()
return|;
else|else
return|return
name|s
return|;
block|}
comment|/** Stem a word contained in a char[].  Returns true if the stemming process    * resulted in a word different from the input.  You can retrieve the    * result with getResultLength()/getResultBuffer() or toString().    */
DECL|method|stem
specifier|public
name|boolean
name|stem
parameter_list|(
name|char
index|[]
name|word
parameter_list|)
block|{
return|return
name|stem
argument_list|(
name|word
argument_list|,
name|word
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Stem a word contained in a portion of a char[] array.  Returns    * true if the stemming process resulted in a word different from    * the input.  You can retrieve the result with    * getResultLength()/getResultBuffer() or toString().    */
DECL|method|stem
specifier|public
name|boolean
name|stem
parameter_list|(
name|char
index|[]
name|wordBuffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|wordLen
parameter_list|)
block|{
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|b
operator|.
name|length
operator|<
name|wordLen
condition|)
block|{
name|b
operator|=
operator|new
name|char
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|wordLen
argument_list|,
name|Character
operator|.
name|BYTES
argument_list|)
index|]
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|wordBuffer
argument_list|,
name|offset
argument_list|,
name|b
argument_list|,
literal|0
argument_list|,
name|wordLen
argument_list|)
expr_stmt|;
name|i
operator|=
name|wordLen
expr_stmt|;
return|return
name|stem
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/** Stem a word contained in a leading portion of a char[] array.    * Returns true if the stemming process resulted in a word different    * from the input.  You can retrieve the result with    * getResultLength()/getResultBuffer() or toString().    */
DECL|method|stem
specifier|public
name|boolean
name|stem
parameter_list|(
name|char
index|[]
name|word
parameter_list|,
name|int
name|wordLen
parameter_list|)
block|{
return|return
name|stem
argument_list|(
name|word
argument_list|,
literal|0
argument_list|,
name|wordLen
argument_list|)
return|;
block|}
comment|/** Stem the word placed into the Stemmer buffer through calls to add().    * Returns true if the stemming process resulted in a word different    * from the input.  You can retrieve the result with    * getResultLength()/getResultBuffer() or toString().    */
DECL|method|stem
specifier|public
name|boolean
name|stem
parameter_list|()
block|{
return|return
name|stem
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|stem
specifier|public
name|boolean
name|stem
parameter_list|(
name|int
name|i0
parameter_list|)
block|{
name|k
operator|=
name|i
operator|-
literal|1
expr_stmt|;
name|k0
operator|=
name|i0
expr_stmt|;
if|if
condition|(
name|k
operator|>
name|k0
operator|+
literal|1
condition|)
block|{
name|step1
argument_list|()
expr_stmt|;
name|step2
argument_list|()
expr_stmt|;
name|step3
argument_list|()
expr_stmt|;
name|step4
argument_list|()
expr_stmt|;
name|step5
argument_list|()
expr_stmt|;
name|step6
argument_list|()
expr_stmt|;
block|}
comment|// Also, a word is considered dirty if we lopped off letters
comment|// Thanks to Ifigenia Vairelles for pointing this out.
if|if
condition|(
name|i
operator|!=
name|k
operator|+
literal|1
condition|)
name|dirty
operator|=
literal|true
expr_stmt|;
name|i
operator|=
name|k
operator|+
literal|1
expr_stmt|;
return|return
name|dirty
return|;
block|}
comment|/* Test program for demonstrating the Stemmer.  It reads a file and    * stems each word, writing the result to standard out.    * Usage: Stemmer file-name   public static void main(String[] args) {     PorterStemmer s = new PorterStemmer();      for (int i = 0; i< args.length; i++) {       try {         InputStream in = new FileInputStream(args[i]);         byte[] buffer = new byte[1024];         int bufferLen, offset, ch;          bufferLen = in.read(buffer);         offset = 0;         s.reset();          while(true) {           if (offset< bufferLen)             ch = buffer[offset++];           else {             bufferLen = in.read(buffer);             offset = 0;             if (bufferLen< 0)               ch = -1;             else               ch = buffer[offset++];           }            if (Character.isLetter((char) ch)) {             s.add(Character.toLowerCase((char) ch));           }           else {              s.stem();              System.out.print(s.toString());              s.reset();              if (ch< 0)                break;              else {                System.out.print((char) ch);              }            }         }          in.close();       }       catch (IOException e) {         System.out.println("error reading " + args[i]);       }     }   }*/
block|}
end_class

end_unit

