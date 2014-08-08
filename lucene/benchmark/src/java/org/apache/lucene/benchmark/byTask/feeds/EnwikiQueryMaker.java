begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|standard
operator|.
name|StandardAnalyzer
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
name|index
operator|.
name|Term
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
name|queryparser
operator|.
name|classic
operator|.
name|QueryParser
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
name|search
operator|.
name|MultiTermQuery
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|WildcardQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanFirstQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanNearQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanTermQuery
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
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
operator|.
name|NewAnalyzerTask
import|;
end_import

begin_comment
comment|/**  * A QueryMaker that uses common and uncommon actual Wikipedia queries for  * searching the English Wikipedia collection. 90 queries total.  */
end_comment

begin_class
DECL|class|EnwikiQueryMaker
specifier|public
class|class
name|EnwikiQueryMaker
extends|extends
name|AbstractQueryMaker
implements|implements
name|QueryMaker
block|{
comment|// common and a few uncommon queries from wikipedia search logs
DECL|field|STANDARD_QUERIES
specifier|private
specifier|static
name|String
index|[]
name|STANDARD_QUERIES
init|=
block|{
literal|"Images catbox gif"
block|,
literal|"Imunisasi haram"
block|,
literal|"Favicon ico"
block|,
literal|"Michael jackson"
block|,
literal|"Unknown artist"
block|,
literal|"Lily Thai"
block|,
literal|"Neda"
block|,
literal|"The Last Song"
block|,
literal|"Metallica"
block|,
literal|"Nicola Tesla"
block|,
literal|"Max B"
block|,
literal|"Skil Corporation"
block|,
literal|"\"The 100 Greatest Artists of All Time\""
block|,
literal|"\"Top 100 Global Universities\""
block|,
literal|"Pink floyd"
block|,
literal|"Bolton Sullivan"
block|,
literal|"Frank Lucas Jr"
block|,
literal|"Drake Woods"
block|,
literal|"Radiohead"
block|,
literal|"George Freeman"
block|,
literal|"Oksana Grigorieva"
block|,
literal|"The Elder Scrolls V"
block|,
literal|"Deadpool"
block|,
literal|"Green day"
block|,
literal|"\"Red hot chili peppers\""
block|,
literal|"Jennifer Bini Taylor"
block|,
literal|"The Paradiso Girls"
block|,
literal|"Queen"
block|,
literal|"3Me4Ph"
block|,
literal|"Paloma Jimenez"
block|,
literal|"AUDI A4"
block|,
literal|"Edith Bouvier Beale: A Life In Pictures"
block|,
literal|"\"Skylar James Deleon\""
block|,
literal|"Simple Explanation"
block|,
literal|"Juxtaposition"
block|,
literal|"The Woody Show"
block|,
literal|"London WITHER"
block|,
literal|"In A Dark Place"
block|,
literal|"George Freeman"
block|,
literal|"LuAnn de Lesseps"
block|,
literal|"Muhammad."
block|,
literal|"U2"
block|,
literal|"List of countries by GDP"
block|,
literal|"Dean Martin Discography"
block|,
literal|"Web 3.0"
block|,
literal|"List of American actors"
block|,
literal|"The Expendables"
block|,
literal|"\"100 Greatest Guitarists of All Time\""
block|,
literal|"Vince Offer."
block|,
literal|"\"List of ZIP Codes in the United States\""
block|,
literal|"Blood type diet"
block|,
literal|"Jennifer Gimenez"
block|,
literal|"List of hobbies"
block|,
literal|"The beatles"
block|,
literal|"Acdc"
block|,
literal|"Nightwish"
block|,
literal|"Iron maiden"
block|,
literal|"Murder Was the Case"
block|,
literal|"Pelvic hernia"
block|,
literal|"Naruto Shippuuden"
block|,
literal|"campaign"
block|,
literal|"Enthesopathy of hip region"
block|,
literal|"operating system"
block|,
literal|"mouse"
block|,
literal|"List of Xbox 360 games without region encoding"
block|,
literal|"Shakepearian sonnet"
block|,
literal|"\"The Monday Night Miracle\""
block|,
literal|"India"
block|,
literal|"Dad's Army"
block|,
literal|"Solanum melanocerasum"
block|,
literal|"\"List of PlayStation Portable Wi-Fi games\""
block|,
literal|"Little Pixie Geldof"
block|,
literal|"Planes, Trains& Automobiles"
block|,
literal|"Freddy Ingalls"
block|,
literal|"The Return of Chef"
block|,
literal|"Nehalem"
block|,
literal|"Turtle"
block|,
literal|"Calculus"
block|,
literal|"Superman-Prime"
block|,
literal|"\"The Losers\""
block|,
literal|"pen-pal"
block|,
literal|"Audio stream input output"
block|,
literal|"lifehouse"
block|,
literal|"50 greatest gunners"
block|,
literal|"Polyfecalia"
block|,
literal|"freeloader"
block|,
literal|"The Filthy Youth"
block|}
decl_stmt|;
DECL|method|getPrebuiltQueries
specifier|private
specifier|static
name|Query
index|[]
name|getPrebuiltQueries
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|WildcardQuery
name|wcq
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"fo*"
argument_list|)
argument_list|)
decl_stmt|;
name|wcq
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_FILTER_REWRITE
argument_list|)
expr_stmt|;
comment|// be wary of unanalyzed text
return|return
operator|new
name|Query
index|[]
block|{
operator|new
name|SpanFirstQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"ford"
argument_list|)
argument_list|)
argument_list|,
literal|5
argument_list|)
block|,
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"night"
argument_list|)
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"trading"
argument_list|)
argument_list|)
block|}
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanFirstQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"ford"
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"credit"
argument_list|)
argument_list|)
block|}
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
block|,
name|wcq
block|, }
return|;
block|}
comment|/**    * Parse the strings containing Lucene queries.    *     * @param qs array of strings containing query expressions    * @param a analyzer to use when parsing queries    * @return array of Lucene queries    */
DECL|method|createQueries
specifier|private
specifier|static
name|Query
index|[]
name|createQueries
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|qs
parameter_list|,
name|Analyzer
name|a
parameter_list|)
block|{
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|,
name|a
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|queries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|qs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|Object
name|query
init|=
name|qs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|query
operator|instanceof
name|String
condition|)
block|{
name|q
operator|=
name|qp
operator|.
name|parse
argument_list|(
operator|(
name|String
operator|)
name|query
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|Query
condition|)
block|{
name|q
operator|=
operator|(
name|Query
operator|)
name|query
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Unsupported Query Type: "
operator|+
name|query
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|q
operator|!=
literal|null
condition|)
block|{
name|queries
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|queries
operator|.
name|toArray
argument_list|(
operator|new
name|Query
index|[
literal|0
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|prepareQueries
specifier|protected
name|Query
index|[]
name|prepareQueries
parameter_list|()
throws|throws
name|Exception
block|{
comment|// analyzer (default is standard analyzer)
name|Analyzer
name|anlzr
init|=
name|NewAnalyzerTask
operator|.
name|createAnalyzer
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"analyzer"
argument_list|,
name|StandardAnalyzer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|queryList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|20
argument_list|)
decl_stmt|;
name|queryList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|STANDARD_QUERIES
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|config
operator|.
name|get
argument_list|(
literal|"enwikiQueryMaker.disableSpanQueries"
argument_list|,
literal|false
argument_list|)
condition|)
name|queryList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|getPrebuiltQueries
argument_list|(
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|createQueries
argument_list|(
name|queryList
argument_list|,
name|anlzr
argument_list|)
return|;
block|}
block|}
end_class

end_unit

