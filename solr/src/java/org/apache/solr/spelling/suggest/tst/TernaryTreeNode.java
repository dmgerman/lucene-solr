begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.spelling.suggest.tst
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
operator|.
name|tst
package|;
end_package

begin_comment
comment|/**  * The class creates a TST node.  * @variable splitchar the character stored by a node.  * @variable loKid a reference object to the node containing character smaller than  * this node's character.  * @variable eqKid a reference object to the node containg character next to this  * node's character as occuring in the inserted token.  * @variable hiKid a reference object to the node containing character higher than  * this node's character.  * @variable token used by leaf nodes to store the complete tokens to be added to   * suggest list while auto-completing the prefix.  */
end_comment

begin_class
DECL|class|TernaryTreeNode
specifier|public
class|class
name|TernaryTreeNode
block|{
DECL|field|splitchar
name|char
name|splitchar
decl_stmt|;
DECL|field|loKid
DECL|field|eqKid
DECL|field|hiKid
name|TernaryTreeNode
name|loKid
decl_stmt|,
name|eqKid
decl_stmt|,
name|hiKid
decl_stmt|;
DECL|field|token
name|String
name|token
decl_stmt|;
DECL|field|val
name|Object
name|val
decl_stmt|;
block|}
end_class

end_unit

