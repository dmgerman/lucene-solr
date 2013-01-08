begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.xml.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
operator|.
name|builders
package|;
end_package

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
name|queryparser
operator|.
name|xml
operator|.
name|DOMUtils
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
name|xml
operator|.
name|ParserException
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
name|xml
operator|.
name|QueryBuilder
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
name|sandbox
operator|.
name|queries
operator|.
name|FuzzyLikeThisQuery
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
name|sandbox
operator|.
name|queries
operator|.
name|SlowFuzzyQuery
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
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Builder for {@link FuzzyLikeThisQuery}  */
end_comment

begin_class
DECL|class|FuzzyLikeThisQueryBuilder
specifier|public
class|class
name|FuzzyLikeThisQueryBuilder
implements|implements
name|QueryBuilder
block|{
DECL|field|DEFAULT_MAX_NUM_TERMS
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_NUM_TERMS
init|=
literal|50
decl_stmt|;
DECL|field|DEFAULT_MIN_SIMILARITY
specifier|private
specifier|static
specifier|final
name|float
name|DEFAULT_MIN_SIMILARITY
init|=
name|SlowFuzzyQuery
operator|.
name|defaultMinSimilarity
decl_stmt|;
DECL|field|DEFAULT_PREFIX_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_PREFIX_LENGTH
init|=
literal|1
decl_stmt|;
DECL|field|DEFAULT_IGNORE_TF
specifier|private
specifier|static
specifier|final
name|boolean
name|DEFAULT_IGNORE_TF
init|=
literal|false
decl_stmt|;
DECL|field|analyzer
specifier|private
specifier|final
name|Analyzer
name|analyzer
decl_stmt|;
DECL|method|FuzzyLikeThisQueryBuilder
specifier|public
name|FuzzyLikeThisQueryBuilder
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|NodeList
name|nl
init|=
name|e
operator|.
name|getElementsByTagName
argument_list|(
literal|"Field"
argument_list|)
decl_stmt|;
name|int
name|maxNumTerms
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"maxNumTerms"
argument_list|,
name|DEFAULT_MAX_NUM_TERMS
argument_list|)
decl_stmt|;
name|FuzzyLikeThisQuery
name|fbq
init|=
operator|new
name|FuzzyLikeThisQuery
argument_list|(
name|maxNumTerms
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|fbq
operator|.
name|setIgnoreTF
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"ignoreTF"
argument_list|,
name|DEFAULT_IGNORE_TF
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|fieldElem
init|=
operator|(
name|Element
operator|)
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|float
name|minSimilarity
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|fieldElem
argument_list|,
literal|"minSimilarity"
argument_list|,
name|DEFAULT_MIN_SIMILARITY
argument_list|)
decl_stmt|;
name|int
name|prefixLength
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|fieldElem
argument_list|,
literal|"prefixLength"
argument_list|,
name|DEFAULT_PREFIX_LENGTH
argument_list|)
decl_stmt|;
name|String
name|fieldName
init|=
name|DOMUtils
operator|.
name|getAttributeWithInheritance
argument_list|(
name|fieldElem
argument_list|,
literal|"fieldName"
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|DOMUtils
operator|.
name|getText
argument_list|(
name|fieldElem
argument_list|)
decl_stmt|;
name|fbq
operator|.
name|addTerms
argument_list|(
name|value
argument_list|,
name|fieldName
argument_list|,
name|minSimilarity
argument_list|,
name|prefixLength
argument_list|)
expr_stmt|;
block|}
name|fbq
operator|.
name|setBoost
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"boost"
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|fbq
return|;
block|}
block|}
end_class

end_unit

