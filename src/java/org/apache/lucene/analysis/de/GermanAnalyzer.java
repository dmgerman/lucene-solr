begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.de
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|de
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|StopFilter
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|standard
operator|.
name|StandardFilter
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
name|analysis
operator|.
name|standard
operator|.
name|StandardTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_comment
comment|/**  * Analyzer for German language. Supports an external list of stopwords (words that  * will not be indexed at all) and an external list of exclusions (word that will  * not be stemmed, but indexed).  * A default set of stopwords is used unless an alternative list is specified, the  * exclusion list is empty by default.  *  * @author    Gerhard Schwarz  * @version   $Id$  */
end_comment

begin_class
DECL|class|GermanAnalyzer
specifier|public
class|class
name|GermanAnalyzer
extends|extends
name|Analyzer
block|{
comment|/**      * List of typical german stopwords.      */
DECL|field|GERMAN_STOP_WORDS
specifier|private
name|String
index|[]
name|GERMAN_STOP_WORDS
init|=
block|{
literal|"einer"
block|,
literal|"eine"
block|,
literal|"eines"
block|,
literal|"einem"
block|,
literal|"einen"
block|,
literal|"der"
block|,
literal|"die"
block|,
literal|"das"
block|,
literal|"dass"
block|,
literal|"daß"
block|,
literal|"du"
block|,
literal|"er"
block|,
literal|"sie"
block|,
literal|"es"
block|,
literal|"was"
block|,
literal|"wer"
block|,
literal|"wie"
block|,
literal|"wir"
block|,
literal|"und"
block|,
literal|"oder"
block|,
literal|"ohne"
block|,
literal|"mit"
block|,
literal|"am"
block|,
literal|"im"
block|,
literal|"in"
block|,
literal|"aus"
block|,
literal|"auf"
block|,
literal|"ist"
block|,
literal|"sein"
block|,
literal|"war"
block|,
literal|"wird"
block|,
literal|"ihr"
block|,
literal|"ihre"
block|,
literal|"ihres"
block|,
literal|"als"
block|,
literal|"für"
block|,
literal|"von"
block|,
literal|"mit"
block|,
literal|"dich"
block|,
literal|"dir"
block|,
literal|"mich"
block|,
literal|"mir"
block|,
literal|"mein"
block|,
literal|"sein"
block|,
literal|"kein"
block|,
literal|"durch"
block|,
literal|"wegen"
block|,
literal|"wird"
block|}
decl_stmt|;
comment|/**      * Contains the stopwords used with the StopFilter.      */
DECL|field|stoptable
specifier|private
name|Hashtable
name|stoptable
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
comment|/**      * Contains words that should be indexed but not stemmed.      */
DECL|field|excltable
specifier|private
name|Hashtable
name|excltable
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
comment|/**      * Builds an analyzer.      */
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|()
block|{
name|stoptable
operator|=
name|StopFilter
operator|.
name|makeStopTable
argument_list|(
name|GERMAN_STOP_WORDS
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds an analyzer with the given stop words.      */
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|(
name|String
index|[]
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
name|StopFilter
operator|.
name|makeStopTable
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds an analyzer with the given stop words.      */
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|(
name|Hashtable
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
name|stopwords
expr_stmt|;
block|}
comment|/**      * Builds an analyzer with the given stop words.      */
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|(
name|File
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
name|WordlistLoader
operator|.
name|getWordtable
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds an exclusionlist from an array of Strings.      */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|String
index|[]
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|StopFilter
operator|.
name|makeStopTable
argument_list|(
name|exclusionlist
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds an exclusionlist from a Hashtable.      */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|Hashtable
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|exclusionlist
expr_stmt|;
block|}
comment|/**      * Builds an exclusionlist from the words contained in the given file.      */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|File
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|WordlistLoader
operator|.
name|getWordtable
argument_list|(
name|exclusionlist
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a TokenStream which tokenizes all the text in the provided Reader.      *      * @return  A TokenStream build from a StandardTokenizer filtered with      *		StandardFilter, StopFilter, GermanStemFilter      */
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|TokenStream
name|result
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|StandardFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
comment|// shouldn't there be a lowercaser before stop word filtering?
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|GermanStemFilter
argument_list|(
name|result
argument_list|,
name|excltable
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

