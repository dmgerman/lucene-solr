begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
package|;
end_package

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
name|List
import|;
end_import

begin_comment
comment|/**  * Represents a range facet result  */
end_comment

begin_class
DECL|class|RangeFacet
specifier|public
specifier|abstract
class|class
name|RangeFacet
parameter_list|<
name|B
parameter_list|,
name|G
parameter_list|>
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|counts
specifier|private
specifier|final
name|List
argument_list|<
name|Count
argument_list|>
name|counts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|start
specifier|private
specifier|final
name|B
name|start
decl_stmt|;
DECL|field|end
specifier|private
specifier|final
name|B
name|end
decl_stmt|;
DECL|field|gap
specifier|private
specifier|final
name|G
name|gap
decl_stmt|;
DECL|field|before
specifier|private
specifier|final
name|Number
name|before
decl_stmt|;
DECL|field|after
specifier|private
specifier|final
name|Number
name|after
decl_stmt|;
DECL|field|between
specifier|private
specifier|final
name|Number
name|between
decl_stmt|;
DECL|method|RangeFacet
specifier|protected
name|RangeFacet
parameter_list|(
name|String
name|name
parameter_list|,
name|B
name|start
parameter_list|,
name|B
name|end
parameter_list|,
name|G
name|gap
parameter_list|,
name|Number
name|before
parameter_list|,
name|Number
name|after
parameter_list|,
name|Number
name|between
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
name|this
operator|.
name|gap
operator|=
name|gap
expr_stmt|;
name|this
operator|.
name|before
operator|=
name|before
expr_stmt|;
name|this
operator|.
name|after
operator|=
name|after
expr_stmt|;
name|this
operator|.
name|between
operator|=
name|between
expr_stmt|;
block|}
DECL|method|addCount
specifier|public
name|void
name|addCount
parameter_list|(
name|String
name|value
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|counts
operator|.
name|add
argument_list|(
operator|new
name|Count
argument_list|(
name|value
argument_list|,
name|count
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getCounts
specifier|public
name|List
argument_list|<
name|Count
argument_list|>
name|getCounts
parameter_list|()
block|{
return|return
name|counts
return|;
block|}
DECL|method|getStart
specifier|public
name|B
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
DECL|method|getEnd
specifier|public
name|B
name|getEnd
parameter_list|()
block|{
return|return
name|end
return|;
block|}
DECL|method|getGap
specifier|public
name|G
name|getGap
parameter_list|()
block|{
return|return
name|gap
return|;
block|}
DECL|method|getBefore
specifier|public
name|Number
name|getBefore
parameter_list|()
block|{
return|return
name|before
return|;
block|}
DECL|method|getAfter
specifier|public
name|Number
name|getAfter
parameter_list|()
block|{
return|return
name|after
return|;
block|}
DECL|method|getBetween
specifier|public
name|Number
name|getBetween
parameter_list|()
block|{
return|return
name|between
return|;
block|}
DECL|class|Numeric
specifier|public
specifier|static
class|class
name|Numeric
extends|extends
name|RangeFacet
argument_list|<
name|Number
argument_list|,
name|Number
argument_list|>
block|{
DECL|method|Numeric
specifier|public
name|Numeric
parameter_list|(
name|String
name|name
parameter_list|,
name|Number
name|start
parameter_list|,
name|Number
name|end
parameter_list|,
name|Number
name|gap
parameter_list|,
name|Number
name|before
parameter_list|,
name|Number
name|after
parameter_list|,
name|Number
name|between
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|gap
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
name|between
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Date
specifier|public
specifier|static
class|class
name|Date
extends|extends
name|RangeFacet
argument_list|<
name|java
operator|.
name|util
operator|.
name|Date
argument_list|,
name|String
argument_list|>
block|{
DECL|method|Date
specifier|public
name|Date
parameter_list|(
name|String
name|name
parameter_list|,
name|java
operator|.
name|util
operator|.
name|Date
name|start
parameter_list|,
name|java
operator|.
name|util
operator|.
name|Date
name|end
parameter_list|,
name|String
name|gap
parameter_list|,
name|Number
name|before
parameter_list|,
name|Number
name|after
parameter_list|,
name|Number
name|between
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|gap
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
name|between
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Count
specifier|public
specifier|static
class|class
name|Count
block|{
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
DECL|field|count
specifier|private
specifier|final
name|int
name|count
decl_stmt|;
DECL|field|rangeFacet
specifier|private
specifier|final
name|RangeFacet
name|rangeFacet
decl_stmt|;
DECL|method|Count
specifier|public
name|Count
parameter_list|(
name|String
name|value
parameter_list|,
name|int
name|count
parameter_list|,
name|RangeFacet
name|rangeFacet
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|rangeFacet
operator|=
name|rangeFacet
expr_stmt|;
block|}
DECL|method|getValue
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|getCount
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
DECL|method|getRangeFacet
specifier|public
name|RangeFacet
name|getRangeFacet
parameter_list|()
block|{
return|return
name|rangeFacet
return|;
block|}
block|}
block|}
end_class

end_unit

