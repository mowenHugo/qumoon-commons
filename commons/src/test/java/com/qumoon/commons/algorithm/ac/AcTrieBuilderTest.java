package com.qumoon.commons.algorithm.ac;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author kevin
 */
public class AcTrieBuilderTest {

  @Test
  public void testBuildGoToTransfer_noWords() {
    AcTrie acTrie = new AcTrie();
    AcTrieBuilder.DEFAULT.buildGoToTransfer(acTrie);
    assertEquals('#', acTrie.getRootNode().getChar());
    assertEquals(0, acTrie.getRootNode().getChildren().size());
    assertEquals(0, acTrie.getRootNode().getOutputWords().size());
    assertEquals(null, acTrie.getRootNode().getSuffixNode());
  }

  @Test
  public void testBuildGoToTransfer_singleWord() {
    AcTrie acTrie = new AcTrie();
    AcTrieBuilder.DEFAULT.buildGoToTransfer(acTrie, "cat");
    // root node
    AcTrieNode root = acTrie.getRootNode();
    assertEquals('#', root.getChar());
    assertEquals(1, root.getChildren().size());
    assertEquals(0, root.getOutputWords().size());
    assertEquals(null, root.getSuffixNode());
    // c node
    AcTrieNode catNode_c = root.getChild('c');
    assertEquals('c', catNode_c.getChar());
    assertEquals(1, catNode_c.getChildren().size());
    assertEquals(0, catNode_c.getOutputWords().size());
    assertEquals(null, catNode_c.getSuffixNode());
    // a node
    AcTrieNode catNode_a = catNode_c.getChild('a');
    assertEquals('a', catNode_a.getChar());
    assertEquals(1, catNode_a.getChildren().size());
    assertEquals(0, catNode_a.getOutputWords().size());
    assertEquals(null, catNode_a.getSuffixNode());
    // t node
    AcTrieNode catNode_t = catNode_a.getChild('t');
    assertEquals('t', catNode_t.getChar());
    assertEquals(0, catNode_t.getChildren().size());
    assertEquals(1, catNode_t.getOutputWords().size());
    assertTrue(catNode_t.getOutputWords().contains("cat"));
    assertEquals(null, catNode_t.getSuffixNode());
  }

  @Test()
  public void testBuildGoToTransfer_mutilWords_noIntersect() {
    AcTrie acTrie = new AcTrie();
    AcTrieBuilder.DEFAULT.buildGoToTransfer(acTrie, "cat", "dog");
    // root node
    AcTrieNode root = acTrie.getRootNode();
    assertEquals('#', root.getChar());
    assertEquals(2, root.getChildren().size());
    assertEquals(0, root.getOutputWords().size());
    assertEquals(null, root.getSuffixNode());
    // c node
    AcTrieNode catNode_c = root.getChild('c');
    assertEquals('c', catNode_c.getChar());
    assertEquals(1, catNode_c.getChildren().size());
    assertEquals(0, catNode_c.getOutputWords().size());
    assertEquals(null, catNode_c.getSuffixNode());
    // a node
    AcTrieNode catNode_a = catNode_c.getChild('a');
    assertEquals('a', catNode_a.getChar());
    assertEquals(1, catNode_a.getChildren().size());
    assertEquals(0, catNode_a.getOutputWords().size());
    assertEquals(null, catNode_a.getSuffixNode());
    // t node
    AcTrieNode catNode_t = catNode_a.getChild('t');
    assertEquals('t', catNode_t.getChar());
    assertEquals(0, catNode_t.getChildren().size());
    assertEquals(1, catNode_t.getOutputWords().size());
    assertTrue(catNode_t.getOutputWords().contains("cat"));
    assertEquals(null, catNode_t.getSuffixNode());

    // d node
    AcTrieNode dogNode_d = root.getChild('d');
    assertEquals('d', dogNode_d.getChar());
    assertEquals(1, dogNode_d.getChildren().size());
    assertEquals(0, dogNode_d.getOutputWords().size());
    assertEquals(null, dogNode_d.getSuffixNode());
    // o node
    AcTrieNode dogNode_o = dogNode_d.getChild('o');
    assertEquals('o', dogNode_o.getChar());
    assertEquals(1, dogNode_o.getChildren().size());
    assertEquals(0, dogNode_o.getOutputWords().size());
    assertEquals(null, dogNode_o.getSuffixNode());
    // g node
    AcTrieNode dogNode_g = dogNode_o.getChild('g');
    assertEquals('g', dogNode_g.getChar());
    assertEquals(0, dogNode_g.getChildren().size());
    assertEquals(1, dogNode_g.getOutputWords().size());
    assertTrue(dogNode_g.getOutputWords().contains("dog"));
    assertEquals(null, dogNode_g.getSuffixNode());
  }

  @Test()
  public void testBuildGoToTransfer_mutilWords_intersect() {
    AcTrie acTrie = new AcTrie();
    AcTrieBuilder.DEFAULT.buildGoToTransfer(acTrie, "cat", "car");
    // root node
    AcTrieNode root = acTrie.getRootNode();
    assertEquals('#', root.getChar());
    assertEquals(1, root.getChildren().size());
    assertEquals(0, root.getOutputWords().size());
    assertEquals(null, root.getSuffixNode());
    // c node
    AcTrieNode catNode_c = root.getChild('c');
    assertEquals('c', catNode_c.getChar());
    assertEquals(1, catNode_c.getChildren().size());
    assertEquals(0, catNode_c.getOutputWords().size());
    assertEquals(null, catNode_c.getSuffixNode());
    // a node
    AcTrieNode catNode_a = catNode_c.getChild('a');
    assertEquals('a', catNode_a.getChar());
    assertEquals(2, catNode_a.getChildren().size());
    assertEquals(0, catNode_a.getOutputWords().size());
    assertEquals(null, catNode_a.getSuffixNode());
    // t node
    AcTrieNode catNode_t = catNode_a.getChild('t');
    assertEquals('t', catNode_t.getChar());
    assertEquals(0, catNode_t.getChildren().size());
    assertEquals(1, catNode_t.getOutputWords().size());
    assertTrue(catNode_t.getOutputWords().contains("cat"));
    assertEquals(null, catNode_t.getSuffixNode());

    // r node
    AcTrieNode carNode_r = catNode_a.getChild('r');
    assertEquals('r', carNode_r.getChar());
    assertEquals(0, carNode_r.getChildren().size());
    assertEquals(1, carNode_r.getOutputWords().size());
    assertTrue(carNode_r.getOutputWords().contains("car"));
    assertEquals(null, carNode_r.getSuffixNode());
  }

  @Test()
  public void testBuildGoToTransfer_mutilWords_continuous() {
    AcTrie acTrie = new AcTrie();
    AcTrieBuilder.DEFAULT.buildGoToTransfer(acTrie, "cat", "cats");
    // root node
    AcTrieNode root = acTrie.getRootNode();
    assertEquals('#', root.getChar());
    assertEquals(1, root.getChildren().size());
    assertEquals(0, root.getOutputWords().size());
    assertEquals(null, root.getSuffixNode());
    // c node
    AcTrieNode catNode_c = root.getChild('c');
    assertEquals('c', catNode_c.getChar());
    assertEquals(1, catNode_c.getChildren().size());
    assertEquals(0, catNode_c.getOutputWords().size());
    assertEquals(null, catNode_c.getSuffixNode());
    // a node
    AcTrieNode catNode_a = catNode_c.getChild('a');
    assertEquals('a', catNode_a.getChar());
    assertEquals(1, catNode_a.getChildren().size());
    assertEquals(0, catNode_a.getOutputWords().size());
    assertEquals(null, catNode_a.getSuffixNode());
    // t node
    AcTrieNode catNode_t = catNode_a.getChild('t');
    assertEquals('t', catNode_t.getChar());
    assertEquals(1, catNode_t.getChildren().size());
    assertEquals(1, catNode_t.getOutputWords().size());
    assertTrue(catNode_t.getOutputWords().contains("cat"));
    assertEquals(null, catNode_t.getSuffixNode());

    // s node
    AcTrieNode catsNode_s = catNode_t.getChild('s');
    assertEquals('s', catsNode_s.getChar());
    assertEquals(0, catsNode_s.getChildren().size());
    assertEquals(1, catsNode_s.getOutputWords().size());
    assertTrue(catsNode_s.getOutputWords().contains("cats"));
    assertEquals(null, catsNode_s.getSuffixNode());
  }

  @Test
  public void testBuildGoToTransfer_dupliWord() {
    AcTrie acTrie = new AcTrie();
    AcTrieBuilder.DEFAULT.buildGoToTransfer(acTrie, "cat", "cat");
    // root node
    AcTrieNode root = acTrie.getRootNode();
    assertEquals('#', root.getChar());
    assertEquals(1, root.getChildren().size());
    assertEquals(0, root.getOutputWords().size());
    assertEquals(null, root.getSuffixNode());
    // c node
    AcTrieNode catNode_c = root.getChild('c');
    assertEquals('c', catNode_c.getChar());
    assertEquals(1, catNode_c.getChildren().size());
    assertEquals(0, catNode_c.getOutputWords().size());
    assertEquals(null, catNode_c.getSuffixNode());
    // a node
    AcTrieNode catNode_a = catNode_c.getChild('a');
    assertEquals('a', catNode_a.getChar());
    assertEquals(1, catNode_a.getChildren().size());
    assertEquals(0, catNode_a.getOutputWords().size());
    assertEquals(null, catNode_a.getSuffixNode());
    // t node
    AcTrieNode catNode_t = catNode_a.getChild('t');
    assertEquals('t', catNode_t.getChar());
    assertEquals(0, catNode_t.getChildren().size());
    assertEquals(1, catNode_t.getOutputWords().size());
    assertTrue(catNode_t.getOutputWords().contains("cat"));
    assertEquals(null, catNode_t.getSuffixNode());
  }

  @Test
  public void testBuildFailureTransfer_onlyRoot() {
    AcTrie acTrie = new AcTrie();
    AcTrieBuilder.DEFAULT.buildFailureTransfer(acTrie);
    assertEquals('#', acTrie.getRootNode().getSuffixNode().getChar());
    assertFalse(acTrie.getRootNode().getSuffixNode().isOutputNode());
  }

  @Test
  public void testBuildFailureTransfer_1L() {
    AcTrie acTrie = new AcTrie();
    acTrie.getRootNode().addChild(new AcTrieNode('a')).addOutputWord("a");
    acTrie.getRootNode().addChild(new AcTrieNode('b')).addOutputWord("b");
    AcTrieBuilder.DEFAULT.buildFailureTransfer(acTrie);
    assertEquals('#', acTrie.getRootNode().getSuffixNode().getChar());
    assertEquals('#', acTrie.getRootNode().getChild('a').getSuffixNode()
        .getChar());
    assertEquals('#', acTrie.getRootNode().getChild('b').getSuffixNode()
        .getChar());
    assertTrue(acTrie.getRootNode().getChild('a').getOutputWords()
                   .contains("a"));
    assertTrue(acTrie.getRootNode().getChild('b').getOutputWords()
                   .contains("b"));
  }

  @Test
  public void testBuildFailureTransfer_2L_t1() {
    AcTrie acTrie = new AcTrie();
    acTrie.getRootNode().addChild(new AcTrieNode('a')).addChild(
        new AcTrieNode('b')).addOutputWord("ab");
    acTrie.getRootNode().addChild(new AcTrieNode('b')).addOutputWord("b");
    AcTrieBuilder.DEFAULT.buildFailureTransfer(acTrie);
    assertEquals('#', acTrie.getRootNode().getSuffixNode().getChar());
    assertEquals('#', acTrie.getRootNode().getChild('a').getSuffixNode()
        .getChar());
    assertEquals('#', acTrie.getRootNode().getChild('b').getSuffixNode()
        .getChar());
    assertTrue(acTrie.getRootNode().getChild('b').getOutputWords()
                   .contains("b"));
    assertEquals('b', acTrie.getRootNode().getChild('a').getChild('b')
        .getSuffixNode().getChar());
    assertTrue(acTrie.getRootNode().getChild('a').getChild('b')
                   .getOutputWords().contains("ab"));
    assertTrue(acTrie.getRootNode().getChild('a').getChild('b')
                   .getOutputWords().contains("b"));
  }

  @Test
  public void testBuildFailureTransfer_2L_t2() {
    AcTrie acTrie = new AcTrie();
    acTrie.getRootNode().addChild(new AcTrieNode('a')).addChild(
        new AcTrieNode('b')).addOutputWord("ab");
    acTrie.getRootNode().addChild(new AcTrieNode('c')).addOutputWord("c");
    AcTrieBuilder.DEFAULT.buildFailureTransfer(acTrie);
    assertEquals('#', acTrie.getRootNode().getSuffixNode().getChar());
    assertEquals('#', acTrie.getRootNode().getChild('a').getSuffixNode()
        .getChar());
    assertEquals('#', acTrie.getRootNode().getChild('c').getSuffixNode()
        .getChar());
    assertTrue(acTrie.getRootNode().getChild('c').getOutputWords()
                   .contains("c"));
    assertEquals('#', acTrie.getRootNode().getChild('a').getChild('b')
        .getSuffixNode().getChar());
    assertTrue(acTrie.getRootNode().getChild('a').getChild('b')
                   .getOutputWords().contains("ab"));
  }
}
