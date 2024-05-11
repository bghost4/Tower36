package com.example.tower36;

import java.util.List;
import java.util.Optional;

public record MoveTreeItem(Optional<MoveTreeItem> parent, List<MoveTreeItem> children, Tower36.Move data) {
}
