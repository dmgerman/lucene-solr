begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestSpanBoostQuery
specifier|public
class|class
name|TestSpanBoostQuery
extends|extends
name|LuceneTestCase
block|{
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
specifier|final
name|float
name|boost
init|=
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
operator|*
literal|3
operator|-
literal|1
decl_stmt|;
name|SpanTermQuery
name|q
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanBoostQuery
name|q1
init|=
operator|new
name|SpanBoostQuery
argument_list|(
name|q
argument_list|,
name|boost
argument_list|)
decl_stmt|;
name|SpanBoostQuery
name|q2
init|=
operator|new
name|SpanBoostQuery
argument_list|(
name|q
argument_list|,
name|boost
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|q1
operator|.
name|getBoost
argument_list|()
argument_list|,
name|q2
operator|.
name|getBoost
argument_list|()
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|float
name|boost2
init|=
name|boost
decl_stmt|;
while|while
condition|(
name|boost
operator|==
name|boost2
condition|)
block|{
name|boost2
operator|=
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
operator|*
literal|3
operator|-
literal|1
expr_stmt|;
block|}
name|SpanBoostQuery
name|q3
init|=
operator|new
name|SpanBoostQuery
argument_list|(
name|q
argument_list|,
name|boost2
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|q1
operator|.
name|equals
argument_list|(
name|q3
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|q1
operator|.
name|hashCode
argument_list|()
operator|==
name|q3
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"foo:bar^2.0"
argument_list|,
operator|new
name|SpanBoostQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|SpanOrQuery
name|bq
init|=
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"spanOr([foo:bar, foo:baz])^2.0"
argument_list|,
operator|new
name|SpanBoostQuery
argument_list|(
name|bq
argument_list|,
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
