begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.clustering.carrot2
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
operator|.
name|carrot2
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|attribute
operator|.
name|AttributeNames
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|attribute
operator|.
name|Processing
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|attribute
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|attribute
operator|.
name|constraint
operator|.
name|IntRange
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

begin_class
annotation|@
name|Bindable
argument_list|(
name|prefix
operator|=
literal|"MockClusteringAlgorithm"
argument_list|)
DECL|class|MockClusteringAlgorithm
specifier|public
class|class
name|MockClusteringAlgorithm
extends|extends
name|ProcessingComponentBase
implements|implements
name|IClusteringAlgorithm
block|{
annotation|@
name|Input
annotation|@
name|Processing
annotation|@
name|Attribute
argument_list|(
name|key
operator|=
name|AttributeNames
operator|.
name|DOCUMENTS
argument_list|)
DECL|field|documents
specifier|private
name|List
argument_list|<
name|Document
argument_list|>
name|documents
decl_stmt|;
annotation|@
name|Output
annotation|@
name|Processing
annotation|@
name|Attribute
argument_list|(
name|key
operator|=
name|AttributeNames
operator|.
name|CLUSTERS
argument_list|)
DECL|field|clusters
specifier|private
name|List
argument_list|<
name|Cluster
argument_list|>
name|clusters
decl_stmt|;
annotation|@
name|Input
annotation|@
name|Processing
annotation|@
name|Attribute
annotation|@
name|IntRange
argument_list|(
name|min
operator|=
literal|1
argument_list|,
name|max
operator|=
literal|5
argument_list|)
DECL|field|depth
specifier|private
name|int
name|depth
init|=
literal|2
decl_stmt|;
annotation|@
name|Input
annotation|@
name|Processing
annotation|@
name|Attribute
annotation|@
name|IntRange
argument_list|(
name|min
operator|=
literal|1
argument_list|,
name|max
operator|=
literal|5
argument_list|)
DECL|field|labels
specifier|private
name|int
name|labels
init|=
literal|1
decl_stmt|;
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|()
throws|throws
name|ProcessingException
block|{
name|clusters
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
if|if
condition|(
name|documents
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|int
name|documentIndex
init|=
literal|1
decl_stmt|;
for|for
control|(
name|Document
name|document
range|:
name|documents
control|)
block|{
name|StringBuilder
name|label
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Cluster "
operator|+
name|documentIndex
argument_list|)
decl_stmt|;
name|Cluster
name|cluster
init|=
name|createCluster
argument_list|(
name|label
operator|.
name|toString
argument_list|()
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|clusters
operator|.
name|add
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|depth
condition|;
name|i
operator|++
control|)
block|{
name|label
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|label
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|Cluster
name|newCluster
init|=
name|createCluster
argument_list|(
name|label
operator|.
name|toString
argument_list|()
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|addSubclusters
argument_list|(
name|createCluster
argument_list|(
name|label
operator|.
name|toString
argument_list|()
argument_list|,
name|document
argument_list|)
argument_list|,
name|newCluster
argument_list|)
expr_stmt|;
name|cluster
operator|=
name|newCluster
expr_stmt|;
block|}
name|documentIndex
operator|++
expr_stmt|;
block|}
block|}
DECL|method|createCluster
specifier|private
name|Cluster
name|createCluster
parameter_list|(
name|String
name|labelBase
parameter_list|,
name|Document
modifier|...
name|documents
parameter_list|)
block|{
name|Cluster
name|cluster
init|=
operator|new
name|Cluster
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
name|labels
condition|;
name|i
operator|++
control|)
block|{
name|cluster
operator|.
name|addPhrases
argument_list|(
name|labelBase
operator|+
literal|"#"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|addDocuments
argument_list|(
name|documents
argument_list|)
expr_stmt|;
return|return
name|cluster
return|;
block|}
block|}
end_class

end_unit

