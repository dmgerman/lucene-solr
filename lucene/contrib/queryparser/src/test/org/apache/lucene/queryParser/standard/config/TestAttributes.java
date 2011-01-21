begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.standard.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|standard
operator|.
name|config
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|_TestUtil
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
name|LuceneTestCase
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
name|FuzzyQuery
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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_class
DECL|class|TestAttributes
specifier|public
class|class
name|TestAttributes
extends|extends
name|LuceneTestCase
block|{
comment|// this checks using reflection API if the defaults are correct
DECL|method|testAttributes
specifier|public
name|void
name|testAttributes
parameter_list|()
block|{
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|AllowLeadingWildcardAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|AllowLeadingWildcardAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#allowLeadingWildcard"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|AnalyzerAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|AnalyzerAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#analyzer"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|BoostAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|BoostAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#boost"
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|DateResolutionAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|DateResolutionAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#dateResolution"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|DefaultOperatorAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|DefaultOperatorAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#operator"
argument_list|,
name|DefaultOperatorAttribute
operator|.
name|Operator
operator|.
name|OR
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|DefaultPhraseSlopAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|DefaultPhraseSlopAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#defaultPhraseSlop"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|FieldBoostMapAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|FieldBoostMapAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#boosts"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|FieldDateResolutionMapAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|FieldDateResolutionMapAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#dateRes"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|FuzzyAttributeImpl
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
name|FuzzyAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#prefixLength"
argument_list|,
name|FuzzyQuery
operator|.
name|defaultPrefixLength
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|FuzzyAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#minSimilarity"
argument_list|,
name|FuzzyQuery
operator|.
name|defaultMinSimilarity
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|LocaleAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|LocaleAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#locale"
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|LowercaseExpandedTermsAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|LowercaseExpandedTermsAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#lowercaseExpandedTerms"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|MultiFieldAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|MultiFieldAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#fields"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|MultiTermRewriteMethodAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|MultiTermRewriteMethodAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#multiTermRewriteMethod"
argument_list|,
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|PositionIncrementsAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|PositionIncrementsAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#positionIncrementsEnabled"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|assertAttributeReflection
argument_list|(
operator|new
name|RangeCollatorAttributeImpl
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|RangeCollatorAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#rangeCollator"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

