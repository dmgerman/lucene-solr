begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|search
operator|.
name|FieldComparator
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
name|IndexSearcher
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
name|SortField
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
name|PriorityQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
import|;
end_import

begin_comment
comment|// used by distributed search to merge results.
end_comment

begin_class
DECL|class|ShardFieldSortedHitQueue
specifier|public
class|class
name|ShardFieldSortedHitQueue
extends|extends
name|PriorityQueue
argument_list|<
name|ShardDoc
argument_list|>
block|{
comment|/** Stores a comparator corresponding to each field being sorted by */
DECL|field|comparators
specifier|protected
name|Comparator
argument_list|<
name|ShardDoc
argument_list|>
index|[]
name|comparators
decl_stmt|;
comment|/** Stores the sort criteria being used. */
DECL|field|fields
specifier|protected
name|SortField
index|[]
name|fields
decl_stmt|;
comment|/** The order of these fieldNames should correspond to the order of sort field values retrieved from the shard */
DECL|field|fieldNames
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|ShardFieldSortedHitQueue
specifier|public
name|ShardFieldSortedHitQueue
parameter_list|(
name|SortField
index|[]
name|fields
parameter_list|,
name|int
name|size
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
specifier|final
name|int
name|n
init|=
name|fields
operator|.
name|length
decl_stmt|;
comment|//noinspection unchecked
name|comparators
operator|=
operator|new
name|Comparator
index|[
name|n
index|]
expr_stmt|;
name|this
operator|.
name|fields
operator|=
operator|new
name|SortField
index|[
name|n
index|]
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
name|n
condition|;
operator|++
name|i
control|)
block|{
comment|// keep track of the named fields
name|SortField
operator|.
name|Type
name|type
init|=
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|!=
name|SortField
operator|.
name|Type
operator|.
name|SCORE
operator|&&
name|type
operator|!=
name|SortField
operator|.
name|Type
operator|.
name|DOC
condition|)
block|{
name|fieldNames
operator|.
name|add
argument_list|(
name|fields
index|[
name|i
index|]
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|fieldname
init|=
name|fields
index|[
name|i
index|]
operator|.
name|getField
argument_list|()
decl_stmt|;
name|comparators
index|[
name|i
index|]
operator|=
name|getCachedComparator
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
if|if
condition|(
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
operator|==
name|SortField
operator|.
name|Type
operator|.
name|STRING
condition|)
block|{
name|this
operator|.
name|fields
index|[
name|i
index|]
operator|=
operator|new
name|SortField
argument_list|(
name|fieldname
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|fields
index|[
name|i
index|]
operator|=
operator|new
name|SortField
argument_list|(
name|fieldname
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("%%%%%%%%%%%%%%%%%% got "+fields[i].getType() +"   for "+ fieldname +"  fields[i].getReverse(): "+fields[i].getReverse());
block|}
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|ShardDoc
name|docA
parameter_list|,
name|ShardDoc
name|docB
parameter_list|)
block|{
comment|// If these docs are from the same shard, then the relative order
comment|// is how they appeared in the response from that shard.
if|if
condition|(
name|docA
operator|.
name|shard
operator|==
name|docB
operator|.
name|shard
condition|)
block|{
comment|// if docA has a smaller position, it should be "larger" so it
comment|// comes before docB.
comment|// This will handle sorting by docid within the same shard
comment|// comment this out to test comparators.
return|return
operator|!
operator|(
name|docA
operator|.
name|orderInShard
operator|<
name|docB
operator|.
name|orderInShard
operator|)
return|;
block|}
comment|// run comparators
specifier|final
name|int
name|n
init|=
name|comparators
operator|.
name|length
decl_stmt|;
name|int
name|c
init|=
literal|0
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
name|n
operator|&&
name|c
operator|==
literal|0
condition|;
name|i
operator|++
control|)
block|{
name|c
operator|=
operator|(
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
operator|)
condition|?
name|comparators
index|[
name|i
index|]
operator|.
name|compare
argument_list|(
name|docB
argument_list|,
name|docA
argument_list|)
else|:
name|comparators
index|[
name|i
index|]
operator|.
name|compare
argument_list|(
name|docA
argument_list|,
name|docB
argument_list|)
expr_stmt|;
block|}
comment|// solve tiebreaks by comparing shards (similar to using docid)
comment|// smaller docid's beat larger ids, so reverse the natural ordering
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
name|c
operator|=
operator|-
name|docA
operator|.
name|shard
operator|.
name|compareTo
argument_list|(
name|docB
operator|.
name|shard
argument_list|)
expr_stmt|;
block|}
return|return
name|c
operator|<
literal|0
return|;
block|}
DECL|method|getCachedComparator
name|Comparator
argument_list|<
name|ShardDoc
argument_list|>
name|getCachedComparator
parameter_list|(
name|SortField
name|sortField
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
block|{
name|SortField
operator|.
name|Type
name|type
init|=
name|sortField
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|SortField
operator|.
name|Type
operator|.
name|SCORE
condition|)
block|{
return|return
parameter_list|(
name|o1
parameter_list|,
name|o2
parameter_list|)
lambda|->
block|{
specifier|final
name|float
name|f1
init|=
name|o1
operator|.
name|score
decl_stmt|;
specifier|final
name|float
name|f2
init|=
name|o2
operator|.
name|score
decl_stmt|;
if|if
condition|(
name|f1
operator|<
name|f2
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|f1
operator|>
name|f2
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|SortField
operator|.
name|Type
operator|.
name|REWRITEABLE
condition|)
block|{
try|try
block|{
name|sortField
operator|=
name|sortField
operator|.
name|rewrite
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Exception rewriting sort field "
operator|+
name|sortField
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|comparatorFieldComparator
argument_list|(
name|sortField
argument_list|)
return|;
block|}
DECL|class|ShardComparator
specifier|abstract
class|class
name|ShardComparator
implements|implements
name|Comparator
argument_list|<
name|ShardDoc
argument_list|>
block|{
DECL|field|sortField
specifier|final
name|SortField
name|sortField
decl_stmt|;
DECL|field|fieldName
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|fieldNum
specifier|final
name|int
name|fieldNum
decl_stmt|;
DECL|method|ShardComparator
specifier|public
name|ShardComparator
parameter_list|(
name|SortField
name|sortField
parameter_list|)
block|{
name|this
operator|.
name|sortField
operator|=
name|sortField
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|sortField
operator|.
name|getField
argument_list|()
expr_stmt|;
name|int
name|fieldNum
init|=
literal|0
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
name|fieldNames
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|fieldNames
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldNum
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
name|this
operator|.
name|fieldNum
operator|=
name|fieldNum
expr_stmt|;
block|}
DECL|method|sortVal
name|Object
name|sortVal
parameter_list|(
name|ShardDoc
name|shardDoc
parameter_list|)
block|{
assert|assert
operator|(
name|shardDoc
operator|.
name|sortFieldValues
operator|.
name|getName
argument_list|(
name|fieldNum
argument_list|)
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|)
assert|;
name|List
name|lst
init|=
operator|(
name|List
operator|)
name|shardDoc
operator|.
name|sortFieldValues
operator|.
name|getVal
argument_list|(
name|fieldNum
argument_list|)
decl_stmt|;
return|return
name|lst
operator|.
name|get
argument_list|(
name|shardDoc
operator|.
name|orderInShard
argument_list|)
return|;
block|}
block|}
DECL|method|comparatorFieldComparator
name|Comparator
argument_list|<
name|ShardDoc
argument_list|>
name|comparatorFieldComparator
parameter_list|(
name|SortField
name|sortField
parameter_list|)
block|{
specifier|final
name|FieldComparator
name|fieldComparator
init|=
name|sortField
operator|.
name|getComparator
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
operator|new
name|ShardComparator
argument_list|(
name|sortField
argument_list|)
block|{
comment|// Since the PriorityQueue keeps the biggest elements by default,
comment|// we need to reverse the field compare ordering so that the
comment|// smallest elements are kept instead of the largest... hence
comment|// the negative sign.
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|ShardDoc
name|o1
parameter_list|,
specifier|final
name|ShardDoc
name|o2
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
operator|-
name|fieldComparator
operator|.
name|compareValues
argument_list|(
name|sortVal
argument_list|(
name|o1
argument_list|)
argument_list|,
name|sortVal
argument_list|(
name|o2
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

