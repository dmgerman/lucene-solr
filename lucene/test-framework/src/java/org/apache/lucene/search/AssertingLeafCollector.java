begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

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
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/** Wraps another Collector and checks that  *  order is respected. */
end_comment

begin_class
DECL|class|AssertingLeafCollector
class|class
name|AssertingLeafCollector
extends|extends
name|FilterLeafCollector
block|{
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|min
specifier|private
specifier|final
name|int
name|min
decl_stmt|;
DECL|field|max
specifier|private
specifier|final
name|int
name|max
decl_stmt|;
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
decl_stmt|;
DECL|field|lastCollected
specifier|private
name|int
name|lastCollected
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|AssertingLeafCollector
name|AssertingLeafCollector
parameter_list|(
name|Random
name|random
parameter_list|,
name|LeafCollector
name|collector
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|super
argument_list|(
name|collector
argument_list|)
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|super
operator|.
name|setScorer
argument_list|(
name|AssertingScorer
operator|.
name|wrap
argument_list|(
name|random
argument_list|,
name|scorer
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|doc
operator|>
name|lastCollected
operator|:
literal|"Out of order : "
operator|+
name|lastCollected
operator|+
literal|" "
operator|+
name|doc
assert|;
assert|assert
name|doc
operator|>=
name|min
operator|:
literal|"Out of range: "
operator|+
name|doc
operator|+
literal|"< "
operator|+
name|min
assert|;
assert|assert
name|doc
operator|<
name|max
operator|:
literal|"Out of range: "
operator|+
name|doc
operator|+
literal|">= "
operator|+
name|max
assert|;
assert|assert
name|scorer
operator|.
name|docID
argument_list|()
operator|==
name|doc
operator|:
literal|"Collected: "
operator|+
name|doc
operator|+
literal|" but scorer: "
operator|+
name|scorer
operator|.
name|docID
argument_list|()
assert|;
name|in
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|lastCollected
operator|=
name|doc
expr_stmt|;
block|}
block|}
end_class

end_unit

