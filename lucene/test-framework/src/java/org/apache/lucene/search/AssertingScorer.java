begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|lang
operator|.
name|ref
operator|.
name|WeakReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Map
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
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
name|AssertingAtomicReader
import|;
end_import

begin_comment
comment|/** Wraps a Scorer with additional checks */
end_comment

begin_class
DECL|class|AssertingScorer
specifier|public
class|class
name|AssertingScorer
extends|extends
name|Scorer
block|{
comment|// we need to track scorers using a weak hash map because otherwise we
comment|// could loose references because of eg.
comment|// AssertingScorer.score(Collector) which needs to delegate to work correctly
DECL|field|ASSERTING_INSTANCES
specifier|private
specifier|static
name|Map
argument_list|<
name|Scorer
argument_list|,
name|WeakReference
argument_list|<
name|AssertingScorer
argument_list|>
argument_list|>
name|ASSERTING_INSTANCES
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|WeakHashMap
argument_list|<
name|Scorer
argument_list|,
name|WeakReference
argument_list|<
name|AssertingScorer
argument_list|>
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|wrap
specifier|public
specifier|static
name|Scorer
name|wrap
parameter_list|(
name|Random
name|random
parameter_list|,
name|Scorer
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
operator|||
name|other
operator|instanceof
name|AssertingScorer
condition|)
block|{
return|return
name|other
return|;
block|}
specifier|final
name|AssertingScorer
name|assertScorer
init|=
operator|new
name|AssertingScorer
argument_list|(
name|random
argument_list|,
name|other
argument_list|)
decl_stmt|;
name|ASSERTING_INSTANCES
operator|.
name|put
argument_list|(
name|other
argument_list|,
operator|new
name|WeakReference
argument_list|<
name|AssertingScorer
argument_list|>
argument_list|(
name|assertScorer
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|assertScorer
return|;
block|}
DECL|method|getAssertingScorer
specifier|static
name|Scorer
name|getAssertingScorer
parameter_list|(
name|Random
name|random
parameter_list|,
name|Scorer
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
operator|||
name|other
operator|instanceof
name|AssertingScorer
condition|)
block|{
return|return
name|other
return|;
block|}
specifier|final
name|WeakReference
argument_list|<
name|AssertingScorer
argument_list|>
name|assertingScorerRef
init|=
name|ASSERTING_INSTANCES
operator|.
name|get
argument_list|(
name|other
argument_list|)
decl_stmt|;
specifier|final
name|AssertingScorer
name|assertingScorer
init|=
name|assertingScorerRef
operator|==
literal|null
condition|?
literal|null
else|:
name|assertingScorerRef
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|assertingScorer
operator|==
literal|null
condition|)
block|{
comment|// can happen in case of memory pressure or if
comment|// scorer1.score(collector) calls
comment|// collector.setScorer(scorer2) with scorer1 != scorer2, such as
comment|// BooleanScorer. In that case we can't enable all assertions
return|return
operator|new
name|AssertingScorer
argument_list|(
name|random
argument_list|,
name|other
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|assertingScorer
return|;
block|}
block|}
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|in
specifier|final
name|Scorer
name|in
decl_stmt|;
DECL|field|docsEnumIn
specifier|final
name|AssertingAtomicReader
operator|.
name|AssertingDocsEnum
name|docsEnumIn
decl_stmt|;
DECL|method|AssertingScorer
specifier|private
name|AssertingScorer
parameter_list|(
name|Random
name|random
parameter_list|,
name|Scorer
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
operator|.
name|weight
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
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|docsEnumIn
operator|=
operator|new
name|AssertingAtomicReader
operator|.
name|AssertingDocsEnum
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|getIn
specifier|public
name|Scorer
name|getIn
parameter_list|()
block|{
return|return
name|in
return|;
block|}
DECL|method|iterating
name|boolean
name|iterating
parameter_list|()
block|{
switch|switch
condition|(
name|docID
argument_list|()
condition|)
block|{
case|case
operator|-
literal|1
case|:
case|case
name|NO_MORE_DOCS
case|:
return|return
literal|false
return|;
default|default:
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|iterating
argument_list|()
assert|;
specifier|final
name|float
name|score
init|=
name|in
operator|.
name|score
argument_list|()
decl_stmt|;
assert|assert
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|score
argument_list|)
assert|;
assert|assert
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|score
argument_list|)
assert|;
return|return
name|score
return|;
block|}
annotation|@
name|Override
DECL|method|getChildren
specifier|public
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
comment|// We cannot hide that we hold a single child, else
comment|// collectors (e.g. ToParentBlockJoinCollector) that
comment|// need to walk the scorer tree will miss/skip the
comment|// Scorer we wrap:
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|in
argument_list|,
literal|"SHOULD"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|iterating
argument_list|()
assert|;
return|return
name|in
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|in
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|docsEnumIn
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|docsEnumIn
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|in
operator|.
name|cost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"AssertingScorer("
operator|+
name|in
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

