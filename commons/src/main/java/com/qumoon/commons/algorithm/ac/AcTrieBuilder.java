package com.qumoon.commons.algorithm.ac;

import com.google.common.collect.Lists;

import java.util.LinkedList;

/**
 * 此类使用AC75算法构造一棵trie树。
 *
 * @author kevin
 */
public final class AcTrieBuilder {

  public static final AcTrieBuilder DEFAULT = new AcTrieBuilder();

  private AcTrieBuilder() {
  }

  /**
   * 根据AC75算法构造一棵trie树。
   *
   * @param words 词表
   * @return trie树
   */
  public AcTrie build(String... words) {
    final AcTrie acTrie = new AcTrie();
    buildGoToTransfer(acTrie, words);
    buildFailureTransfer(acTrie);
    return acTrie;
  }

  /**
   * 构造转换转移。
   */
  public void buildGoToTransfer(AcTrie acTrie, String... words) {

    for (String word : words) {
      AcTrieNode currentNode = acTrie.getRootNode();
      char[] wordCharArray = word.toCharArray();
      for (int i = 0; i < wordCharArray.length; i++) {
        char aChar = wordCharArray[i];
        AcTrieNode childNode = currentNode.getChild(aChar);
        if (null != childNode) {
          if (i == wordCharArray.length - 1) {
            childNode.addOutputWord(word);
          } else {
            currentNode = childNode;
          }
        } else {
          AcTrieNode newNode = new AcTrieNode(aChar);
          if (i == wordCharArray.length - 1) {
            newNode.addOutputWord(word);
          }
          currentNode.addChild(newNode);
          currentNode = newNode;
        }
      }
    }
  }

  /**
   * 构造失败转移
   */
  public void buildFailureTransfer(AcTrie acTrie) {
    // 初始化根结点
    AcTrieNode root = acTrie.getRootNode();
    root.setSuffixNode(root);
    // 初始化第一层节点的失败转移
    LinkedList<AcTrieNode> queue = Lists.newLinkedList();
    for (AcTrieNode rootChild : root.getChildren()) {
      rootChild.setSuffixNode(root);
      queue.offerLast(rootChild);
    }
    // 对其他节点进行广度优先构建失败转移
    while (!queue.isEmpty()) {
      AcTrieNode current = queue.pollFirst();
      for (AcTrieNode child : current.getChildren()) {
        char childChar = child.getChar();
        queue.offerLast(child);
        AcTrieNode suffix = current.getSuffixNode();
        AcTrieNode suffixChild = suffix.getChild(childChar);
        while (null == suffixChild) {
          suffix = suffix.getSuffixNode();
          suffixChild = suffix.getChild(childChar);
          if (suffix.isRoot()) {
            break;
          }
        }
        if (suffix.isRoot() && null == suffixChild) {
          child.setSuffixNode(suffix);
        } else {
          child.setSuffixNode(suffixChild);
          if (suffixChild.isOutputNode()) {
            child.addOutputWords(suffixChild.getOutputWords());
          }
        }
      }
    }
  }
}
