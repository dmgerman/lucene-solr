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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
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
name|schema
operator|.
name|SchemaField
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
name|search
operator|.
name|SolrIndexSearcher
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
name|search
operator|.
name|DocSet
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
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|util
operator|.
name|SimpleOrderedMap
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
operator|.
name|ErrorCode
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
name|params
operator|.
name|SolrParams
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
name|params
operator|.
name|FacetParams
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
name|request
operator|.
name|SimpleFacets
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
name|request
operator|.
name|SolrQueryRequest
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
name|schema
operator|.
name|FieldType
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
name|TermQuery
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
name|TermRangeQuery
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
name|solr
operator|.
name|search
operator|.
name|SyntaxError
import|;
end_import

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
name|Deque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * @since solr 4.0  */
end_comment

begin_class
DECL|class|PivotFacetHelper
specifier|public
class|class
name|PivotFacetHelper
extends|extends
name|SimpleFacets
block|{
DECL|field|minMatch
specifier|protected
name|int
name|minMatch
decl_stmt|;
DECL|method|PivotFacetHelper
specifier|public
name|PivotFacetHelper
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|super
argument_list|(
name|req
argument_list|,
name|docs
argument_list|,
name|params
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|minMatch
operator|=
name|params
operator|.
name|getInt
argument_list|(
name|FacetParams
operator|.
name|FACET_PIVOT_MINCOUNT
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|process
specifier|public
name|SimpleOrderedMap
argument_list|<
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|process
parameter_list|(
name|String
index|[]
name|pivots
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|rb
operator|.
name|doFacets
operator|||
name|pivots
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|SimpleOrderedMap
argument_list|<
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|pivotResponse
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|pivot
range|:
name|pivots
control|)
block|{
comment|//ex: pivot == "features,cat" or even "{!ex=mytag}features,cat"
try|try
block|{
name|this
operator|.
name|parseParams
argument_list|(
name|FacetParams
operator|.
name|FACET_PIVOT
argument_list|,
name|pivot
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyntaxError
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|pivot
operator|=
name|facetValue
expr_stmt|;
comment|//facetValue potentially modified from parseParams()
name|String
index|[]
name|fields
init|=
name|pivot
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Pivot Facet needs at least two fields: "
operator|+
name|pivot
argument_list|)
throw|;
block|}
name|String
name|field
init|=
name|fields
index|[
literal|0
index|]
decl_stmt|;
name|String
name|subField
init|=
name|fields
index|[
literal|1
index|]
decl_stmt|;
name|Deque
argument_list|<
name|String
argument_list|>
name|fnames
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|fields
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>
literal|1
condition|;
name|i
operator|--
control|)
block|{
name|fnames
operator|.
name|push
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|superFacets
init|=
name|this
operator|.
name|getTermCounts
argument_list|(
name|field
argument_list|)
decl_stmt|;
comment|//super.key usually == pivot unless local-param 'key' used
name|pivotResponse
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|doPivots
argument_list|(
name|superFacets
argument_list|,
name|field
argument_list|,
name|subField
argument_list|,
name|fnames
argument_list|,
name|docs
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|pivotResponse
return|;
block|}
comment|/**    * Recursive function to do all the pivots    */
DECL|method|doPivots
specifier|protected
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|doPivots
parameter_list|(
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|superFacets
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|subField
parameter_list|,
name|Deque
argument_list|<
name|String
argument_list|>
name|fnames
parameter_list|,
name|DocSet
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrIndexSearcher
name|searcher
init|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
comment|// TODO: optimize to avoid converting to an external string and then having to convert back to internal below
name|SchemaField
name|sfield
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|FieldType
name|ftype
init|=
name|sfield
operator|.
name|getType
argument_list|()
decl_stmt|;
name|String
name|nextField
init|=
name|fnames
operator|.
name|poll
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|superFacets
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|kv
range|:
name|superFacets
control|)
block|{
comment|// Only sub-facet if parent facet has positive count - still may not be any values for the sub-field though
if|if
condition|(
name|kv
operator|.
name|getValue
argument_list|()
operator|>=
name|minMatch
condition|)
block|{
comment|// may be null when using facet.missing
specifier|final
name|String
name|fieldValue
init|=
name|kv
operator|.
name|getKey
argument_list|()
decl_stmt|;
comment|// don't reuse the same BytesRef each time since we will be
comment|// constructing Term objects used in TermQueries that may be cached.
name|BytesRef
name|termval
init|=
literal|null
decl_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|pivot
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|pivot
operator|.
name|add
argument_list|(
literal|"field"
argument_list|,
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|fieldValue
condition|)
block|{
name|pivot
operator|.
name|add
argument_list|(
literal|"value"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termval
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
name|ftype
operator|.
name|readableToIndexed
argument_list|(
name|fieldValue
argument_list|,
name|termval
argument_list|)
expr_stmt|;
name|pivot
operator|.
name|add
argument_list|(
literal|"value"
argument_list|,
name|ftype
operator|.
name|toObject
argument_list|(
name|sfield
argument_list|,
name|termval
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|pivot
operator|.
name|add
argument_list|(
literal|"count"
argument_list|,
name|kv
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|subField
operator|==
literal|null
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|pivot
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DocSet
name|subset
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|termval
condition|)
block|{
name|DocSet
name|hasVal
init|=
name|searcher
operator|.
name|getDocSet
argument_list|(
operator|new
name|TermRangeQuery
argument_list|(
name|field
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|subset
operator|=
name|docs
operator|.
name|andNot
argument_list|(
name|hasVal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termval
argument_list|)
argument_list|)
decl_stmt|;
name|subset
operator|=
name|searcher
operator|.
name|getDocSet
argument_list|(
name|query
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|docs
operator|=
name|subset
expr_stmt|;
comment|//used by getTermCounts()
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|nl
init|=
name|this
operator|.
name|getTermCounts
argument_list|(
name|subField
argument_list|)
decl_stmt|;
if|if
condition|(
name|nl
operator|.
name|size
argument_list|()
operator|>=
name|minMatch
condition|)
block|{
name|pivot
operator|.
name|add
argument_list|(
literal|"pivot"
argument_list|,
name|doPivots
argument_list|(
name|nl
argument_list|,
name|subField
argument_list|,
name|nextField
argument_list|,
name|fnames
argument_list|,
name|subset
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|pivot
argument_list|)
expr_stmt|;
comment|// only add response if there are some counts
block|}
block|}
block|}
block|}
comment|// put the field back on the list
name|fnames
operator|.
name|push
argument_list|(
name|nextField
argument_list|)
expr_stmt|;
return|return
name|values
return|;
block|}
comment|// TODO: This is code from various patches to support distributed search.
comment|//  Some parts may be helpful for whoever implements distributed search.
comment|//
comment|//  @Override
comment|//  public int distributedProcess(ResponseBuilder rb) throws IOException {
comment|//    if (!rb.doFacets) {
comment|//      return ResponseBuilder.STAGE_DONE;
comment|//    }
comment|//
comment|//    if (rb.stage == ResponseBuilder.STAGE_GET_FIELDS) {
comment|//      SolrParams params = rb.req.getParams();
comment|//      String[] pivots = params.getParams(FacetParams.FACET_PIVOT);
comment|//      for ( ShardRequest sreq : rb.outgoing ) {
comment|//        if (( sreq.purpose& ShardRequest.PURPOSE_GET_FIELDS ) != 0
comment|//&& sreq.shards != null&& sreq.shards.length == 1 ) {
comment|//          sreq.params.set( FacetParams.FACET, "true" );
comment|//          sreq.params.set( FacetParams.FACET_PIVOT, pivots );
comment|//          sreq.params.set( FacetParams.FACET_PIVOT_MINCOUNT, 1 ); // keep this at 1 regardless so that it accumulates everything
comment|//            }
comment|//      }
comment|//    }
comment|//    return ResponseBuilder.STAGE_DONE;
comment|//  }
comment|//
comment|//  @Override
comment|//  public void handleResponses(ResponseBuilder rb, ShardRequest sreq) {
comment|//    if (!rb.doFacets) return;
comment|//
comment|//
comment|//    if ((sreq.purpose& ShardRequest.PURPOSE_GET_FACETS)!=0) {
comment|//      SimpleOrderedMap<List<NamedList<Object>>> tf = rb._pivots;
comment|//      if ( null == tf ) {
comment|//        tf = new SimpleOrderedMap<List<NamedList<Object>>>();
comment|//        rb._pivots = tf;
comment|//      }
comment|//      for (ShardResponse srsp: sreq.responses) {
comment|//        int shardNum = rb.getShardNum(srsp.getShard());
comment|//
comment|//        NamedList facet_counts = (NamedList)srsp.getSolrResponse().getResponse().get("facet_counts");
comment|//
comment|//        // handle facet trees from shards
comment|//        SimpleOrderedMap<List<NamedList<Object>>> shard_pivots =
comment|//          (SimpleOrderedMap<List<NamedList<Object>>>)facet_counts.get( PIVOT_KEY );
comment|//
comment|//        if ( shard_pivots != null ) {
comment|//          for (int j=0; j< shard_pivots.size(); j++) {
comment|//            // TODO -- accumulate the results from each shard
comment|//            // The following code worked to accumulate facets for an previous
comment|//            // two level patch... it is here for reference till someone can upgrade
comment|//            /**
comment|//            String shard_tree_name = (String) shard_pivots.getName( j );
comment|//            SimpleOrderedMap<NamedList> shard_tree = (SimpleOrderedMap<NamedList>)shard_pivots.getVal( j );
comment|//            SimpleOrderedMap<NamedList> facet_tree = tf.get( shard_tree_name );
comment|//            if ( null == facet_tree) {
comment|//              facet_tree = new SimpleOrderedMap<NamedList>();
comment|//              tf.add( shard_tree_name, facet_tree );
comment|//            }
comment|//
comment|//            for( int o = 0; o< shard_tree.size() ; o++ ) {
comment|//              String shard_outer = (String) shard_tree.getName( o );
comment|//              NamedList shard_innerList = (NamedList) shard_tree.getVal( o );
comment|//              NamedList tree_innerList  = (NamedList) facet_tree.get( shard_outer );
comment|//              if ( null == tree_innerList ) {
comment|//                tree_innerList = new NamedList();
comment|//                facet_tree.add( shard_outer, tree_innerList );
comment|//              }
comment|//
comment|//              for ( int i = 0 ; i< shard_innerList.size() ; i++ ) {
comment|//                String shard_term = (String) shard_innerList.getName( i );
comment|//                long shard_count  = ((Number) shard_innerList.getVal(i)).longValue();
comment|//                int tree_idx      = tree_innerList.indexOf( shard_term, 0 );
comment|//
comment|//                if ( -1 == tree_idx ) {
comment|//                  tree_innerList.add( shard_term, shard_count );
comment|//                } else {
comment|//                  long tree_count = ((Number) tree_innerList.getVal( tree_idx )).longValue();
comment|//                  tree_innerList.setVal( tree_idx, shard_count + tree_count );
comment|//                }
comment|//              } // innerList loop
comment|//            } // outer loop
comment|//              **/
comment|//          } // each tree loop
comment|//        }
comment|//      }
comment|//    }
comment|//    return ;
comment|//  }
comment|//
comment|//  @Override
comment|//  public void finishStage(ResponseBuilder rb) {
comment|//    if (!rb.doFacets || rb.stage != ResponseBuilder.STAGE_GET_FIELDS) return;
comment|//    // wait until STAGE_GET_FIELDS
comment|//    // so that "result" is already stored in the response (for aesthetics)
comment|//
comment|//    SimpleOrderedMap<List<NamedList<Object>>> tf = rb._pivots;
comment|//
comment|//    // get 'facet_counts' from the response
comment|//    NamedList facetCounts = (NamedList) rb.rsp.getValues().get("facet_counts");
comment|//    if (facetCounts == null) {
comment|//      facetCounts = new NamedList();
comment|//      rb.rsp.add("facet_counts", facetCounts);
comment|//    }
comment|//    facetCounts.add( PIVOT_KEY, tf );
comment|//    rb._pivots = null;
comment|//  }
comment|//
comment|//  public String getDescription() {
comment|//    return "Handle Pivot (multi-level) Faceting";
comment|//  }
block|}
end_class

end_unit

