begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ru
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ru
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Russian stemming algorithm implementation (see http://snowball.sourceforge.net for detailed description).  */
end_comment

begin_class
DECL|class|RussianStemmer
class|class
name|RussianStemmer
block|{
comment|// positions of RV, R1 and R2 respectively
DECL|field|RV
DECL|field|R1
DECL|field|R2
specifier|private
name|int
name|RV
decl_stmt|,
name|R1
decl_stmt|,
name|R2
decl_stmt|;
comment|// letters (currently unused letters are commented out)
DECL|field|A
specifier|private
specifier|final
specifier|static
name|char
name|A
init|=
literal|'\u0430'
decl_stmt|;
comment|//private final static char B = '\u0431';
DECL|field|V
specifier|private
specifier|final
specifier|static
name|char
name|V
init|=
literal|'\u0432'
decl_stmt|;
DECL|field|G
specifier|private
specifier|final
specifier|static
name|char
name|G
init|=
literal|'\u0433'
decl_stmt|;
comment|//private final static char D = '\u0434';
DECL|field|E
specifier|private
specifier|final
specifier|static
name|char
name|E
init|=
literal|'\u0435'
decl_stmt|;
comment|//private final static char ZH = '\u0436';
comment|//private final static char Z = '\u0437';
DECL|field|I
specifier|private
specifier|final
specifier|static
name|char
name|I
init|=
literal|'\u0438'
decl_stmt|;
DECL|field|I_
specifier|private
specifier|final
specifier|static
name|char
name|I_
init|=
literal|'\u0439'
decl_stmt|;
comment|//private final static char K = '\u043A';
DECL|field|L
specifier|private
specifier|final
specifier|static
name|char
name|L
init|=
literal|'\u043B'
decl_stmt|;
DECL|field|M
specifier|private
specifier|final
specifier|static
name|char
name|M
init|=
literal|'\u043C'
decl_stmt|;
DECL|field|N
specifier|private
specifier|final
specifier|static
name|char
name|N
init|=
literal|'\u043D'
decl_stmt|;
DECL|field|O
specifier|private
specifier|final
specifier|static
name|char
name|O
init|=
literal|'\u043E'
decl_stmt|;
comment|//private final static char P = '\u043F';
comment|//private final static char R = '\u0440';
DECL|field|S
specifier|private
specifier|final
specifier|static
name|char
name|S
init|=
literal|'\u0441'
decl_stmt|;
DECL|field|T
specifier|private
specifier|final
specifier|static
name|char
name|T
init|=
literal|'\u0442'
decl_stmt|;
DECL|field|U
specifier|private
specifier|final
specifier|static
name|char
name|U
init|=
literal|'\u0443'
decl_stmt|;
comment|//private final static char F = '\u0444';
DECL|field|X
specifier|private
specifier|final
specifier|static
name|char
name|X
init|=
literal|'\u0445'
decl_stmt|;
comment|//private final static char TS = '\u0446';
comment|//private final static char CH = '\u0447';
DECL|field|SH
specifier|private
specifier|final
specifier|static
name|char
name|SH
init|=
literal|'\u0448'
decl_stmt|;
DECL|field|SHCH
specifier|private
specifier|final
specifier|static
name|char
name|SHCH
init|=
literal|'\u0449'
decl_stmt|;
comment|//private final static char HARD = '\u044A';
DECL|field|Y
specifier|private
specifier|final
specifier|static
name|char
name|Y
init|=
literal|'\u044B'
decl_stmt|;
DECL|field|SOFT
specifier|private
specifier|final
specifier|static
name|char
name|SOFT
init|=
literal|'\u044C'
decl_stmt|;
DECL|field|AE
specifier|private
specifier|final
specifier|static
name|char
name|AE
init|=
literal|'\u044D'
decl_stmt|;
DECL|field|IU
specifier|private
specifier|final
specifier|static
name|char
name|IU
init|=
literal|'\u044E'
decl_stmt|;
DECL|field|IA
specifier|private
specifier|final
specifier|static
name|char
name|IA
init|=
literal|'\u044F'
decl_stmt|;
comment|// stem definitions
DECL|field|vowels
specifier|private
specifier|static
name|char
index|[]
name|vowels
init|=
block|{
name|A
block|,
name|E
block|,
name|I
block|,
name|O
block|,
name|U
block|,
name|Y
block|,
name|AE
block|,
name|IU
block|,
name|IA
block|}
decl_stmt|;
DECL|field|perfectiveGerundEndings1
specifier|private
specifier|static
name|char
index|[]
index|[]
name|perfectiveGerundEndings1
init|=
block|{
block|{
name|V
block|}
block|,
block|{
name|V
block|,
name|SH
block|,
name|I
block|}
block|,
block|{
name|V
block|,
name|SH
block|,
name|I
block|,
name|S
block|,
name|SOFT
block|}
block|}
decl_stmt|;
DECL|field|perfectiveGerund1Predessors
specifier|private
specifier|static
name|char
index|[]
index|[]
name|perfectiveGerund1Predessors
init|=
block|{
block|{
name|A
block|}
block|,
block|{
name|IA
block|}
block|}
decl_stmt|;
DECL|field|perfectiveGerundEndings2
specifier|private
specifier|static
name|char
index|[]
index|[]
name|perfectiveGerundEndings2
init|=
block|{
block|{
name|I
block|,
name|V
block|}
block|,
block|{
name|Y
block|,
name|V
block|}
block|,
block|{
name|I
block|,
name|V
block|,
name|SH
block|,
name|I
block|}
block|,
block|{
name|Y
block|,
name|V
block|,
name|SH
block|,
name|I
block|}
block|,
block|{
name|I
block|,
name|V
block|,
name|SH
block|,
name|I
block|,
name|S
block|,
name|SOFT
block|}
block|,
block|{
name|Y
block|,
name|V
block|,
name|SH
block|,
name|I
block|,
name|S
block|,
name|SOFT
block|}
block|}
decl_stmt|;
DECL|field|adjectiveEndings
specifier|private
specifier|static
name|char
index|[]
index|[]
name|adjectiveEndings
init|=
block|{
block|{
name|E
block|,
name|E
block|}
block|,
block|{
name|I
block|,
name|E
block|}
block|,
block|{
name|Y
block|,
name|E
block|}
block|,
block|{
name|O
block|,
name|E
block|}
block|,
block|{
name|E
block|,
name|I_
block|}
block|,
block|{
name|I
block|,
name|I_
block|}
block|,
block|{
name|Y
block|,
name|I_
block|}
block|,
block|{
name|O
block|,
name|I_
block|}
block|,
block|{
name|E
block|,
name|M
block|}
block|,
block|{
name|I
block|,
name|M
block|}
block|,
block|{
name|Y
block|,
name|M
block|}
block|,
block|{
name|O
block|,
name|M
block|}
block|,
block|{
name|I
block|,
name|X
block|}
block|,
block|{
name|Y
block|,
name|X
block|}
block|,
block|{
name|U
block|,
name|IU
block|}
block|,
block|{
name|IU
block|,
name|IU
block|}
block|,
block|{
name|A
block|,
name|IA
block|}
block|,
block|{
name|IA
block|,
name|IA
block|}
block|,
block|{
name|O
block|,
name|IU
block|}
block|,
block|{
name|E
block|,
name|IU
block|}
block|,
block|{
name|I
block|,
name|M
block|,
name|I
block|}
block|,
block|{
name|Y
block|,
name|M
block|,
name|I
block|}
block|,
block|{
name|E
block|,
name|G
block|,
name|O
block|}
block|,
block|{
name|O
block|,
name|G
block|,
name|O
block|}
block|,
block|{
name|E
block|,
name|M
block|,
name|U
block|}
block|,
block|{
name|O
block|,
name|M
block|,
name|U
block|}
block|}
decl_stmt|;
DECL|field|participleEndings1
specifier|private
specifier|static
name|char
index|[]
index|[]
name|participleEndings1
init|=
block|{
block|{
name|SHCH
block|}
block|,
block|{
name|E
block|,
name|M
block|}
block|,
block|{
name|N
block|,
name|N
block|}
block|,
block|{
name|V
block|,
name|SH
block|}
block|,
block|{
name|IU
block|,
name|SHCH
block|}
block|}
decl_stmt|;
DECL|field|participleEndings2
specifier|private
specifier|static
name|char
index|[]
index|[]
name|participleEndings2
init|=
block|{
block|{
name|I
block|,
name|V
block|,
name|SH
block|}
block|,
block|{
name|Y
block|,
name|V
block|,
name|SH
block|}
block|,
block|{
name|U
block|,
name|IU
block|,
name|SHCH
block|}
block|}
decl_stmt|;
DECL|field|participle1Predessors
specifier|private
specifier|static
name|char
index|[]
index|[]
name|participle1Predessors
init|=
block|{
block|{
name|A
block|}
block|,
block|{
name|IA
block|}
block|}
decl_stmt|;
DECL|field|reflexiveEndings
specifier|private
specifier|static
name|char
index|[]
index|[]
name|reflexiveEndings
init|=
block|{
block|{
name|S
block|,
name|IA
block|}
block|,
block|{
name|S
block|,
name|SOFT
block|}
block|}
decl_stmt|;
DECL|field|verbEndings1
specifier|private
specifier|static
name|char
index|[]
index|[]
name|verbEndings1
init|=
block|{
block|{
name|I_
block|}
block|,
block|{
name|L
block|}
block|,
block|{
name|N
block|}
block|,
block|{
name|L
block|,
name|O
block|}
block|,
block|{
name|N
block|,
name|O
block|}
block|,
block|{
name|E
block|,
name|T
block|}
block|,
block|{
name|IU
block|,
name|T
block|}
block|,
block|{
name|L
block|,
name|A
block|}
block|,
block|{
name|N
block|,
name|A
block|}
block|,
block|{
name|L
block|,
name|I
block|}
block|,
block|{
name|E
block|,
name|M
block|}
block|,
block|{
name|N
block|,
name|Y
block|}
block|,
block|{
name|E
block|,
name|T
block|,
name|E
block|}
block|,
block|{
name|I_
block|,
name|T
block|,
name|E
block|}
block|,
block|{
name|T
block|,
name|SOFT
block|}
block|,
block|{
name|E
block|,
name|SH
block|,
name|SOFT
block|}
block|,
block|{
name|N
block|,
name|N
block|,
name|O
block|}
block|}
decl_stmt|;
DECL|field|verbEndings2
specifier|private
specifier|static
name|char
index|[]
index|[]
name|verbEndings2
init|=
block|{
block|{
name|IU
block|}
block|,
block|{
name|U
block|,
name|IU
block|}
block|,
block|{
name|E
block|,
name|N
block|}
block|,
block|{
name|E
block|,
name|I_
block|}
block|,
block|{
name|IA
block|,
name|T
block|}
block|,
block|{
name|U
block|,
name|I_
block|}
block|,
block|{
name|I
block|,
name|L
block|}
block|,
block|{
name|Y
block|,
name|L
block|}
block|,
block|{
name|I
block|,
name|M
block|}
block|,
block|{
name|Y
block|,
name|M
block|}
block|,
block|{
name|I
block|,
name|T
block|}
block|,
block|{
name|Y
block|,
name|T
block|}
block|,
block|{
name|I
block|,
name|L
block|,
name|A
block|}
block|,
block|{
name|Y
block|,
name|L
block|,
name|A
block|}
block|,
block|{
name|E
block|,
name|N
block|,
name|A
block|}
block|,
block|{
name|I
block|,
name|T
block|,
name|E
block|}
block|,
block|{
name|I
block|,
name|L
block|,
name|I
block|}
block|,
block|{
name|Y
block|,
name|L
block|,
name|I
block|}
block|,
block|{
name|I
block|,
name|L
block|,
name|O
block|}
block|,
block|{
name|Y
block|,
name|L
block|,
name|O
block|}
block|,
block|{
name|E
block|,
name|N
block|,
name|O
block|}
block|,
block|{
name|U
block|,
name|E
block|,
name|T
block|}
block|,
block|{
name|U
block|,
name|IU
block|,
name|T
block|}
block|,
block|{
name|E
block|,
name|N
block|,
name|Y
block|}
block|,
block|{
name|I
block|,
name|T
block|,
name|SOFT
block|}
block|,
block|{
name|Y
block|,
name|T
block|,
name|SOFT
block|}
block|,
block|{
name|I
block|,
name|SH
block|,
name|SOFT
block|}
block|,
block|{
name|E
block|,
name|I_
block|,
name|T
block|,
name|E
block|}
block|,
block|{
name|U
block|,
name|I_
block|,
name|T
block|,
name|E
block|}
block|}
decl_stmt|;
DECL|field|verb1Predessors
specifier|private
specifier|static
name|char
index|[]
index|[]
name|verb1Predessors
init|=
block|{
block|{
name|A
block|}
block|,
block|{
name|IA
block|}
block|}
decl_stmt|;
DECL|field|nounEndings
specifier|private
specifier|static
name|char
index|[]
index|[]
name|nounEndings
init|=
block|{
block|{
name|A
block|}
block|,
block|{
name|U
block|}
block|,
block|{
name|I_
block|}
block|,
block|{
name|O
block|}
block|,
block|{
name|U
block|}
block|,
block|{
name|E
block|}
block|,
block|{
name|Y
block|}
block|,
block|{
name|I
block|}
block|,
block|{
name|SOFT
block|}
block|,
block|{
name|IA
block|}
block|,
block|{
name|E
block|,
name|V
block|}
block|,
block|{
name|O
block|,
name|V
block|}
block|,
block|{
name|I
block|,
name|E
block|}
block|,
block|{
name|SOFT
block|,
name|E
block|}
block|,
block|{
name|IA
block|,
name|X
block|}
block|,
block|{
name|I
block|,
name|IU
block|}
block|,
block|{
name|E
block|,
name|I
block|}
block|,
block|{
name|I
block|,
name|I
block|}
block|,
block|{
name|E
block|,
name|I_
block|}
block|,
block|{
name|O
block|,
name|I_
block|}
block|,
block|{
name|E
block|,
name|M
block|}
block|,
block|{
name|A
block|,
name|M
block|}
block|,
block|{
name|O
block|,
name|M
block|}
block|,
block|{
name|A
block|,
name|X
block|}
block|,
block|{
name|SOFT
block|,
name|IU
block|}
block|,
block|{
name|I
block|,
name|IA
block|}
block|,
block|{
name|SOFT
block|,
name|IA
block|}
block|,
block|{
name|I
block|,
name|I_
block|}
block|,
block|{
name|IA
block|,
name|M
block|}
block|,
block|{
name|IA
block|,
name|M
block|,
name|I
block|}
block|,
block|{
name|A
block|,
name|M
block|,
name|I
block|}
block|,
block|{
name|I
block|,
name|E
block|,
name|I_
block|}
block|,
block|{
name|I
block|,
name|IA
block|,
name|M
block|}
block|,
block|{
name|I
block|,
name|E
block|,
name|M
block|}
block|,
block|{
name|I
block|,
name|IA
block|,
name|X
block|}
block|,
block|{
name|I
block|,
name|IA
block|,
name|M
block|,
name|I
block|}
block|}
decl_stmt|;
DECL|field|superlativeEndings
specifier|private
specifier|static
name|char
index|[]
index|[]
name|superlativeEndings
init|=
block|{
block|{
name|E
block|,
name|I_
block|,
name|SH
block|}
block|,
block|{
name|E
block|,
name|I_
block|,
name|SH
block|,
name|E
block|}
block|}
decl_stmt|;
DECL|field|derivationalEndings
specifier|private
specifier|static
name|char
index|[]
index|[]
name|derivationalEndings
init|=
block|{
block|{
name|O
block|,
name|S
block|,
name|T
block|}
block|,
block|{
name|O
block|,
name|S
block|,
name|T
block|,
name|SOFT
block|}
block|}
decl_stmt|;
comment|/**      * RussianStemmer constructor comment.      */
DECL|method|RussianStemmer
specifier|public
name|RussianStemmer
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * Adjectival ending is an adjective ending,      * optionally preceded by participle ending.      * Creation date: (17/03/2002 12:14:58 AM)      * @param stemmingZone java.lang.StringBuilder      */
DECL|method|adjectival
specifier|private
name|boolean
name|adjectival
parameter_list|(
name|StringBuilder
name|stemmingZone
parameter_list|)
block|{
comment|// look for adjective ending in a stemming zone
if|if
condition|(
operator|!
name|findAndRemoveEnding
argument_list|(
name|stemmingZone
argument_list|,
name|adjectiveEndings
argument_list|)
condition|)
return|return
literal|false
return|;
comment|// if adjective ending was found, try for participle ending.
comment|// variable r is unused, we are just interested in the side effect of
comment|// findAndRemoveEnding():
name|boolean
name|r
init|=
name|findAndRemoveEnding
argument_list|(
name|stemmingZone
argument_list|,
name|participleEndings1
argument_list|,
name|participle1Predessors
argument_list|)
operator|||
name|findAndRemoveEnding
argument_list|(
name|stemmingZone
argument_list|,
name|participleEndings2
argument_list|)
decl_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Derivational endings      * Creation date: (17/03/2002 12:14:58 AM)      * @param stemmingZone java.lang.StringBuilder      */
DECL|method|derivational
specifier|private
name|boolean
name|derivational
parameter_list|(
name|StringBuilder
name|stemmingZone
parameter_list|)
block|{
name|int
name|endingLength
init|=
name|findEnding
argument_list|(
name|stemmingZone
argument_list|,
name|derivationalEndings
argument_list|)
decl_stmt|;
if|if
condition|(
name|endingLength
operator|==
literal|0
condition|)
comment|// no derivational ending found
return|return
literal|false
return|;
else|else
block|{
comment|// Ensure that the ending locates in R2
if|if
condition|(
name|R2
operator|-
name|RV
operator|<=
name|stemmingZone
operator|.
name|length
argument_list|()
operator|-
name|endingLength
condition|)
block|{
name|stemmingZone
operator|.
name|setLength
argument_list|(
name|stemmingZone
operator|.
name|length
argument_list|()
operator|-
name|endingLength
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
comment|/**      * Finds ending among given ending class and returns the length of ending found(0, if not found).      * Creation date: (17/03/2002 8:18:34 PM)      */
DECL|method|findEnding
specifier|private
name|int
name|findEnding
parameter_list|(
name|StringBuilder
name|stemmingZone
parameter_list|,
name|int
name|startIndex
parameter_list|,
name|char
index|[]
index|[]
name|theEndingClass
parameter_list|)
block|{
name|boolean
name|match
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|theEndingClass
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|char
index|[]
name|theEnding
init|=
name|theEndingClass
index|[
name|i
index|]
decl_stmt|;
comment|// check if the ending is bigger than stemming zone
if|if
condition|(
name|startIndex
operator|<
name|theEnding
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|match
operator|=
literal|false
expr_stmt|;
continue|continue;
block|}
name|match
operator|=
literal|true
expr_stmt|;
name|int
name|stemmingIndex
init|=
name|startIndex
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|theEnding
operator|.
name|length
operator|-
literal|1
init|;
name|j
operator|>=
literal|0
condition|;
name|j
operator|--
control|)
block|{
if|if
condition|(
name|stemmingZone
operator|.
name|charAt
argument_list|(
name|stemmingIndex
operator|--
argument_list|)
operator|!=
name|theEnding
index|[
name|j
index|]
condition|)
block|{
name|match
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
comment|// check if ending was found
if|if
condition|(
name|match
condition|)
block|{
return|return
name|theEndingClass
index|[
name|i
index|]
operator|.
name|length
return|;
comment|// cut ending
block|}
block|}
return|return
literal|0
return|;
block|}
DECL|method|findEnding
specifier|private
name|int
name|findEnding
parameter_list|(
name|StringBuilder
name|stemmingZone
parameter_list|,
name|char
index|[]
index|[]
name|theEndingClass
parameter_list|)
block|{
return|return
name|findEnding
argument_list|(
name|stemmingZone
argument_list|,
name|stemmingZone
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|,
name|theEndingClass
argument_list|)
return|;
block|}
comment|/**      * Finds the ending among the given class of endings and removes it from stemming zone.      * Creation date: (17/03/2002 8:18:34 PM)      */
DECL|method|findAndRemoveEnding
specifier|private
name|boolean
name|findAndRemoveEnding
parameter_list|(
name|StringBuilder
name|stemmingZone
parameter_list|,
name|char
index|[]
index|[]
name|theEndingClass
parameter_list|)
block|{
name|int
name|endingLength
init|=
name|findEnding
argument_list|(
name|stemmingZone
argument_list|,
name|theEndingClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|endingLength
operator|==
literal|0
condition|)
comment|// not found
return|return
literal|false
return|;
else|else
block|{
name|stemmingZone
operator|.
name|setLength
argument_list|(
name|stemmingZone
operator|.
name|length
argument_list|()
operator|-
name|endingLength
argument_list|)
expr_stmt|;
comment|// cut the ending found
return|return
literal|true
return|;
block|}
block|}
comment|/**      * Finds the ending among the given class of endings, then checks if this ending was      * preceded by any of given predecessors, and if so, removes it from stemming zone.      * Creation date: (17/03/2002 8:18:34 PM)      */
DECL|method|findAndRemoveEnding
specifier|private
name|boolean
name|findAndRemoveEnding
parameter_list|(
name|StringBuilder
name|stemmingZone
parameter_list|,
name|char
index|[]
index|[]
name|theEndingClass
parameter_list|,
name|char
index|[]
index|[]
name|thePredessors
parameter_list|)
block|{
name|int
name|endingLength
init|=
name|findEnding
argument_list|(
name|stemmingZone
argument_list|,
name|theEndingClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|endingLength
operator|==
literal|0
condition|)
comment|// not found
return|return
literal|false
return|;
else|else
block|{
name|int
name|predessorLength
init|=
name|findEnding
argument_list|(
name|stemmingZone
argument_list|,
name|stemmingZone
operator|.
name|length
argument_list|()
operator|-
name|endingLength
operator|-
literal|1
argument_list|,
name|thePredessors
argument_list|)
decl_stmt|;
if|if
condition|(
name|predessorLength
operator|==
literal|0
condition|)
return|return
literal|false
return|;
else|else
block|{
name|stemmingZone
operator|.
name|setLength
argument_list|(
name|stemmingZone
operator|.
name|length
argument_list|()
operator|-
name|endingLength
argument_list|)
expr_stmt|;
comment|// cut the ending found
return|return
literal|true
return|;
block|}
block|}
block|}
comment|/**      * Marks positions of RV, R1 and R2 in a given word.      * Creation date: (16/03/2002 3:40:11 PM)      */
DECL|method|markPositions
specifier|private
name|void
name|markPositions
parameter_list|(
name|String
name|word
parameter_list|)
block|{
name|RV
operator|=
literal|0
expr_stmt|;
name|R1
operator|=
literal|0
expr_stmt|;
name|R2
operator|=
literal|0
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|// find RV
while|while
condition|(
name|word
operator|.
name|length
argument_list|()
operator|>
name|i
operator|&&
operator|!
name|isVowel
argument_list|(
name|word
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|word
operator|.
name|length
argument_list|()
operator|-
literal|1
operator|<
operator|++
name|i
condition|)
return|return;
comment|// RV zone is empty
name|RV
operator|=
name|i
expr_stmt|;
comment|// find R1
while|while
condition|(
name|word
operator|.
name|length
argument_list|()
operator|>
name|i
operator|&&
name|isVowel
argument_list|(
name|word
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|word
operator|.
name|length
argument_list|()
operator|-
literal|1
operator|<
operator|++
name|i
condition|)
return|return;
comment|// R1 zone is empty
name|R1
operator|=
name|i
expr_stmt|;
comment|// find R2
while|while
condition|(
name|word
operator|.
name|length
argument_list|()
operator|>
name|i
operator|&&
operator|!
name|isVowel
argument_list|(
name|word
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|word
operator|.
name|length
argument_list|()
operator|-
literal|1
operator|<
operator|++
name|i
condition|)
return|return;
comment|// R2 zone is empty
while|while
condition|(
name|word
operator|.
name|length
argument_list|()
operator|>
name|i
operator|&&
name|isVowel
argument_list|(
name|word
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|word
operator|.
name|length
argument_list|()
operator|-
literal|1
operator|<
operator|++
name|i
condition|)
return|return;
comment|// R2 zone is empty
name|R2
operator|=
name|i
expr_stmt|;
block|}
comment|/**      * Checks if character is a vowel..      * Creation date: (16/03/2002 10:47:03 PM)      * @return boolean      * @param letter char      */
DECL|method|isVowel
specifier|private
name|boolean
name|isVowel
parameter_list|(
name|char
name|letter
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vowels
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|letter
operator|==
name|vowels
index|[
name|i
index|]
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Noun endings.      * Creation date: (17/03/2002 12:14:58 AM)      * @param stemmingZone java.lang.StringBuilder      */
DECL|method|noun
specifier|private
name|boolean
name|noun
parameter_list|(
name|StringBuilder
name|stemmingZone
parameter_list|)
block|{
return|return
name|findAndRemoveEnding
argument_list|(
name|stemmingZone
argument_list|,
name|nounEndings
argument_list|)
return|;
block|}
comment|/**      * Perfective gerund endings.      * Creation date: (17/03/2002 12:14:58 AM)      * @param stemmingZone java.lang.StringBuilder      */
DECL|method|perfectiveGerund
specifier|private
name|boolean
name|perfectiveGerund
parameter_list|(
name|StringBuilder
name|stemmingZone
parameter_list|)
block|{
return|return
name|findAndRemoveEnding
argument_list|(
name|stemmingZone
argument_list|,
name|perfectiveGerundEndings1
argument_list|,
name|perfectiveGerund1Predessors
argument_list|)
operator|||
name|findAndRemoveEnding
argument_list|(
name|stemmingZone
argument_list|,
name|perfectiveGerundEndings2
argument_list|)
return|;
block|}
comment|/**      * Reflexive endings.      * Creation date: (17/03/2002 12:14:58 AM)      * @param stemmingZone java.lang.StringBuilder      */
DECL|method|reflexive
specifier|private
name|boolean
name|reflexive
parameter_list|(
name|StringBuilder
name|stemmingZone
parameter_list|)
block|{
return|return
name|findAndRemoveEnding
argument_list|(
name|stemmingZone
argument_list|,
name|reflexiveEndings
argument_list|)
return|;
block|}
comment|/**      * Insert the method's description here.      * Creation date: (17/03/2002 12:14:58 AM)      * @param stemmingZone java.lang.StringBuilder      */
DECL|method|removeI
specifier|private
name|boolean
name|removeI
parameter_list|(
name|StringBuilder
name|stemmingZone
parameter_list|)
block|{
if|if
condition|(
name|stemmingZone
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|stemmingZone
operator|.
name|charAt
argument_list|(
name|stemmingZone
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
name|I
condition|)
block|{
name|stemmingZone
operator|.
name|setLength
argument_list|(
name|stemmingZone
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * Insert the method's description here.      * Creation date: (17/03/2002 12:14:58 AM)      * @param stemmingZone java.lang.StringBuilder      */
DECL|method|removeSoft
specifier|private
name|boolean
name|removeSoft
parameter_list|(
name|StringBuilder
name|stemmingZone
parameter_list|)
block|{
if|if
condition|(
name|stemmingZone
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|stemmingZone
operator|.
name|charAt
argument_list|(
name|stemmingZone
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
name|SOFT
condition|)
block|{
name|stemmingZone
operator|.
name|setLength
argument_list|(
name|stemmingZone
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * Finds the stem for given Russian word.      * Creation date: (16/03/2002 3:36:48 PM)      * @return java.lang.String      * @param input java.lang.String      */
DECL|method|stem
specifier|public
name|String
name|stem
parameter_list|(
name|String
name|input
parameter_list|)
block|{
name|markPositions
argument_list|(
name|input
argument_list|)
expr_stmt|;
if|if
condition|(
name|RV
operator|==
literal|0
condition|)
return|return
name|input
return|;
comment|//RV wasn't detected, nothing to stem
name|StringBuilder
name|stemmingZone
init|=
operator|new
name|StringBuilder
argument_list|(
name|input
operator|.
name|substring
argument_list|(
name|RV
argument_list|)
argument_list|)
decl_stmt|;
comment|// stemming goes on in RV
comment|// Step 1
if|if
condition|(
operator|!
name|perfectiveGerund
argument_list|(
name|stemmingZone
argument_list|)
condition|)
block|{
name|reflexive
argument_list|(
name|stemmingZone
argument_list|)
expr_stmt|;
comment|// variable r is unused, we are just interested in the flow that gets
comment|// created by logical expression: apply adjectival(); if that fails,
comment|// apply verb() etc
name|boolean
name|r
init|=
name|adjectival
argument_list|(
name|stemmingZone
argument_list|)
operator|||
name|verb
argument_list|(
name|stemmingZone
argument_list|)
operator|||
name|noun
argument_list|(
name|stemmingZone
argument_list|)
decl_stmt|;
block|}
comment|// Step 2
name|removeI
argument_list|(
name|stemmingZone
argument_list|)
expr_stmt|;
comment|// Step 3
name|derivational
argument_list|(
name|stemmingZone
argument_list|)
expr_stmt|;
comment|// Step 4
name|superlative
argument_list|(
name|stemmingZone
argument_list|)
expr_stmt|;
name|undoubleN
argument_list|(
name|stemmingZone
argument_list|)
expr_stmt|;
name|removeSoft
argument_list|(
name|stemmingZone
argument_list|)
expr_stmt|;
comment|// return result
return|return
name|input
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|RV
argument_list|)
operator|+
name|stemmingZone
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Superlative endings.      * Creation date: (17/03/2002 12:14:58 AM)      * @param stemmingZone java.lang.StringBuilder      */
DECL|method|superlative
specifier|private
name|boolean
name|superlative
parameter_list|(
name|StringBuilder
name|stemmingZone
parameter_list|)
block|{
return|return
name|findAndRemoveEnding
argument_list|(
name|stemmingZone
argument_list|,
name|superlativeEndings
argument_list|)
return|;
block|}
comment|/**      * Undoubles N.      * Creation date: (17/03/2002 12:14:58 AM)      * @param stemmingZone java.lang.StringBuilder      */
DECL|method|undoubleN
specifier|private
name|boolean
name|undoubleN
parameter_list|(
name|StringBuilder
name|stemmingZone
parameter_list|)
block|{
name|char
index|[]
index|[]
name|doubleN
init|=
block|{
block|{
name|N
block|,
name|N
block|}
block|}
decl_stmt|;
if|if
condition|(
name|findEnding
argument_list|(
name|stemmingZone
argument_list|,
name|doubleN
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|stemmingZone
operator|.
name|setLength
argument_list|(
name|stemmingZone
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * Verb endings.      * Creation date: (17/03/2002 12:14:58 AM)      * @param stemmingZone java.lang.StringBuilder      */
DECL|method|verb
specifier|private
name|boolean
name|verb
parameter_list|(
name|StringBuilder
name|stemmingZone
parameter_list|)
block|{
return|return
name|findAndRemoveEnding
argument_list|(
name|stemmingZone
argument_list|,
name|verbEndings1
argument_list|,
name|verb1Predessors
argument_list|)
operator|||
name|findAndRemoveEnding
argument_list|(
name|stemmingZone
argument_list|,
name|verbEndings2
argument_list|)
return|;
block|}
comment|/**      * Static method for stemming.      */
DECL|method|stemWord
specifier|public
specifier|static
name|String
name|stemWord
parameter_list|(
name|String
name|theWord
parameter_list|)
block|{
name|RussianStemmer
name|stemmer
init|=
operator|new
name|RussianStemmer
argument_list|()
decl_stmt|;
return|return
name|stemmer
operator|.
name|stem
argument_list|(
name|theWord
argument_list|)
return|;
block|}
block|}
end_class

end_unit

